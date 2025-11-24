# PDF Converter Worker

Worker service để xử lý chuyển đổi PDF sang DOCX, chạy độc lập trên máy riêng.

## Yêu cầu

- Java 21+
- Maven 3.6+
- MySQL Database (cùng database với Server)
- Quyền truy cập vào thư mục chứa file PDF (shared folder hoặc network path)

## Cấu hình

### 1. Cấu hình Database

Worker cần kết nối đến cùng database với Server. Cấu hình qua environment variables:

```bash
# Windows
set DB_HOST=192.168.1.100
set DB_PORT=3306
set DB_NAME=pdf_converter
set DB_USER=root
set DB_PASSWORD=your_password

# Linux/Mac
export DB_HOST=192.168.1.100
export DB_PORT=3306
export DB_NAME=pdf_converter
export DB_USER=root
export DB_PASSWORD=your_password
```

### 2. Cấu hình Port

Mặc định Worker chạy trên port 8081. Có thể thay đổi:

```bash
# Windows
set WORKER_PORT=8081

# Linux/Mac
export WORKER_PORT=8081
```

### 3. Cấu hình File Path

**QUAN TRỌNG**: Worker và Server phải có quyền truy cập vào cùng thư mục chứa file PDF.

Có 2 cách:

#### Cách 1: Shared Network Folder (Khuyến nghị)
- Server upload file vào network share (ví dụ: `\\192.168.1.100\pdf_uploads\`)
- Worker đọc từ cùng network share
- Cả 2 máy đều có quyền read/write

#### Cách 2: File Transfer
- Server upload file lên local
- Copy file sang Worker qua network (FTP, SCP, etc.)
- Worker xử lý và copy kết quả về Server

## Build và Chạy

### Build

```bash
cd worker
mvn clean package
```

File JAR sẽ được tạo tại: `target/pdf-converter-worker-1.0-SNAPSHOT.jar`

### Chạy

```bash
# Windows
java -jar target/pdf-converter-worker-1.0-SNAPSHOT.jar

# Linux/Mac
java -jar target/pdf-converter-worker-1.0-SNAPSHOT.jar
```

### Chạy với cấu hình tùy chỉnh

```bash
# Windows
set DB_HOST=192.168.1.100
set DB_PASSWORD=mypassword
set WORKER_PORT=8081
java -jar target/pdf-converter-worker-1.0-SNAPSHOT.jar

# Linux/Mac
export DB_HOST=192.168.1.100
export DB_PASSWORD=mypassword
export WORKER_PORT=8081
java -jar target/pdf-converter-worker-1.0-SNAPSHOT.jar
```

## API Endpoints

### Health Check
```
GET /health
```
Kiểm tra Worker có đang chạy không.

Response:
```json
{
  "status": "OK",
  "message": "Worker is running"
}
```

### Convert Task
```
POST /api/convert
Content-Type: application/json

{
  "taskId": 123,
  "pdfPath": "C:/uploads/file.pdf",
  "callbackUrl": "http://server:9090/convert_pdf_to_word/api/worker/callback"
}
```

Response (202 Accepted):
```json
{
  "success": true,
  "message": "Task accepted and processing"
}
```

### Worker Status
```
GET /api/status
```

Response:
```json
{
  "activeTasks": 2,
  "completedTasks": 100,
  "failedTasks": 5
}
```

## Kiểm tra Worker

```bash
# Health check
curl http://localhost:8081/health

# Status
curl http://localhost:8081/api/status
```

## Logs

Worker sẽ in logs ra console:
- `[WORKER]` - Logs từ Worker
- `[WORKER-DAO]` - Database operations
- `[DEBUG]` - Debug information

## Troubleshooting

### Worker không kết nối được database
- Kiểm tra DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
- Đảm bảo MySQL cho phép remote connections
- Kiểm tra firewall

### Worker không đọc được file PDF
- Kiểm tra file path có đúng không
- Kiểm tra quyền truy cập file
- Nếu dùng network share, đảm bảo cả 2 máy đều có quyền

### Worker không gửi callback được
- Kiểm tra callbackUrl có đúng không
- Kiểm tra Server có đang chạy không
- Kiểm tra firewall giữa Worker và Server

