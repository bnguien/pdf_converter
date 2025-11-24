# Hướng dẫn chạy Server và Worker trên 2 máy riêng biệt

## Kiến trúc

```
┌─────────────────┐         HTTP/REST         ┌─────────────────┐
│                 │ ────────────────────────> │                 │
│   Server        │                           │   Worker        │
│   (Máy 1)       │ <──────────────────────── │   (Máy 2)       │
│                 │      Callback/Status      │                 │
└─────────────────┘                           └─────────────────┘
       │                                              │
       │                                              │
       v                                              v
┌─────────────────┐                           ┌─────────────────┐
│   MySQL DB      │                           │   File System   │
│   (Shared)      │                           │   (Shared)      │
└─────────────────┘                           └─────────────────┘
```

## Yêu cầu

### Máy Server (Máy 1)
- Java 21+
- Maven 3.6+
- Tomcat hoặc Servlet Container
- Kết nối đến MySQL Database
- Quyền upload file vào shared folder

### Máy Worker (Máy 2)
- Java 21+
- Maven 3.6+
- Kết nối đến cùng MySQL Database
- Quyền đọc/ghi file trong shared folder
- Có thể truy cập được từ Server qua network

### Database (Có thể trên máy riêng hoặc cùng máy Server)
- MySQL 8.0+
- Database: `pdf_converter`
- Cho phép remote connections từ cả Server và Worker

## Bước 1: Cấu hình Database

### 1.1. Tạo database (nếu chưa có)
```sql
CREATE DATABASE pdf_converter;
USE pdf_converter;
-- Import schema từ database/schema.sql
```

### 1.2. Cấu hình MySQL cho phép remote connections

Trên MySQL server, chỉnh sửa `my.cnf` hoặc `my.ini`:
```ini
[mysqld]
bind-address = 0.0.0.0  # Cho phép kết nối từ mọi IP
```

Tạo user cho remote access:
```sql
CREATE USER 'admin'@'%' IDENTIFIED BY 'admin123';
GRANT ALL PRIVILEGES ON pdf_converter.* TO 'admin'@'%';
FLUSH PRIVILEGES;
```

### 1.3. Cấu hình Firewall
Mở port 3306 trên MySQL server để Server và Worker có thể kết nối.

## Bước 2: Cấu hình Shared File Storage

###  Network Share (Windows)

**Trên máy Server:**
1. Tạo thư mục: `C:\pdf_uploads`
2. Share folder:
   - Right-click folder → Properties → Sharing
   - Share với Everyone (hoặc user cụ thể)
   - Ghi chú network path: `\\SERVER_IP\pdf_uploads`

**Trên máy Worker:**
1. Map network drive hoặc truy cập trực tiếp: `\\SERVER_IP\pdf_uploads`
2. Đảm bảo có quyền read/write

**Cấu hình trong Server:**
Sửa `UploadController.java` để upload vào network share:
```java
String folderUpload = "\\\\SERVER_IP\\pdf_uploads";  // Windows
// hoặc
String folderUpload = "/mnt/pdf_uploads";  // Linux mount point
```


## Bước 3: Cấu hình và Build Server

### 3.1. Cấu hình Database connection

Sửa `src/main/java/util/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://DB_SERVER_IP:3306/pdf_converter?useSSL=false&useUnicode=true&characterEncoding=UTF-8";
private static final String USER = "admin";
private static final String PASSWORD = "admin123";
```

### 3.2. Cấu hình Worker URL

Tạo/sửa `src/main/resources/worker.properties`:
```properties
worker.url=http://WORKER_IP:8081
```

Hoặc set environment variable:
```bash
# Windows
set WORKER_URL=http://192.168.1.101:8081

# Linux/Mac
export WORKER_URL=http://192.168.1.101:8081
```

### 3.3. Build Server
```bash
cd convert_pdf_to_word
mvn clean package
```

### 3.4. Deploy Server
Copy `target/convert_pdf_to_word.war` vào Tomcat `webapps/` folder.

## Bước 4: Cấu hình và Build Worker

### 4.1. Cấu hình Database

Set environment variables trên máy Worker:
```bash
# Windows
set DB_HOST=DB_SERVER_IP
set DB_PORT=3306
set DB_NAME=pdf_converter
set DB_USER=admin
set DB_PASSWORD=admin123


### 4.2. Cấu hình Port (optional)
```bash
# Windows
set WORKER_PORT=8081

# Linux/Mac
export WORKER_PORT=8081
```

### 4.3. Build Worker
```bash
cd worker
mvn clean package
```

### 4.4. Chạy Worker
```bash
java -jar target/pdf-converter-worker-1.0-SNAPSHOT.jar
```

Worker sẽ chạy trên port 8081 (hoặc port đã cấu hình).

## Bước 5: Kiểm tra kết nối

### 5.1. Kiểm tra Worker
```bash
# Từ máy bất kỳ
curl http://WORKER_IP:8081/health

# Response mong đợi:
# {"status":"OK","message":"Worker is running"}
```

### 5.2. Kiểm tra Server có thấy Worker không
Truy cập Server và thử upload + convert một file PDF. Kiểm tra logs:
- Server logs: `[SERVER] Worker available, sending task to worker`
- Worker logs: `[WORKER] Received convert task`

## Bước 6: Firewall Configuration

### Trên máy Server
- Mở port 9090 (hoặc port Tomcat của bạn) cho HTTP requests
- Cho phép outbound connections đến Worker (port 8081)

### Trên máy Worker
- Mở port 8081 cho HTTP requests từ Server
- Cho phép outbound connections đến MySQL (port 3306)
- Cho phép outbound connections đến Server (port 9090) cho callback

### Trên máy Database
- Mở port 3306 cho MySQL connections từ Server và Worker

## Troubleshooting

### Server không kết nối được Worker
1. Kiểm tra Worker có đang chạy: `curl http://WORKER_IP:8081/health`
2. Kiểm tra firewall trên máy Worker
3. Kiểm tra `WORKER_URL` trong Server config
4. Kiểm tra network connectivity: `ping WORKER_IP`

### Worker không đọc được file PDF
1. Kiểm tra file path có đúng không
2. Kiểm tra quyền truy cập shared folder
3. Kiểm tra file có tồn tại không trên máy Worker
4. Test: Tạo file test trên Server, kiểm tra Worker có thấy không

### Worker không update database được
1. Kiểm tra DB connection: `mysql -h DB_HOST -u DB_USER -p`
2. Kiểm tra firewall trên máy Database
3. Kiểm tra MySQL user có quyền UPDATE không

### Callback không hoạt động
1. Kiểm tra Server có đang chạy không
2. Kiểm tra callback URL có đúng không
3. Kiểm tra firewall cho phép Worker → Server connection
4. Kiểm tra logs trên Server: `[SERVER] Received callback from worker`

## Fallback Mode

Nếu Worker không khả dụng, Server sẽ tự động fallback về local conversion (xử lý trên chính máy Server). Điều này đảm bảo hệ thống vẫn hoạt động ngay cả khi Worker down.

## Monitoring

### Worker Status
```bash
curl http://WORKER_IP:8081/api/status
```

Response:
```json
{
  "activeTasks": 2,
  "completedTasks": 100,
  "failedTasks": 5
}
```

### Server Logs
Theo dõi logs để biết:
- Task được gửi đến Worker hay xử lý local
- Callback từ Worker có thành công không

## Mở rộng

Có thể chạy nhiều Worker instances để tăng throughput:
1. Chạy Worker trên nhiều máy với port khác nhau
2. Cấu hình load balancer trước các Workers
3. Hoặc implement worker selection logic trong Server

