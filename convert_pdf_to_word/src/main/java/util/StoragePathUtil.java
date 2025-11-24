package util;

import java.io.File;

/**
 * Utility class để quản lý đường dẫn lưu trữ file thống nhất
 * Sử dụng biến môi trường PROJECT_STORAGE_ROOT
 */
public class StoragePathUtil {
    
    private static final String ENV_VAR_NAME = "PROJECT_STORAGE_ROOT";
    private static final String SAVED_FILES_FOLDER = "SavedFiles";
    
    /**
     * Lấy giá trị từ System Property hoặc Environment Variable
     * @return Giá trị của PROJECT_STORAGE_ROOT hoặc null nếu chưa cấu hình
     */
    private static String resolveStorageRoot() {
        String value = System.getProperty(ENV_VAR_NAME);
        System.out.println("DEBUG CHECK: System Property Value: " + value); 
        if (value == null || value.isBlank()) {
            value = System.getenv(ENV_VAR_NAME);
            System.out.println("DEBUG CHECK: Environment Variable Value: " + value); 
        }
        
        if (value != null) {
            value = value.trim();
            if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1);
            }
            value = value.trim();
        }
        
        return value;
    }
    
    /**
     * Đảm bảo biến môi trường đã được cấu hình, nếu không thì throw lỗi với hướng dẫn rõ ràng
     */
    private static String ensureStorageRoot() {
        String uploadRoot = resolveStorageRoot();
        if (uploadRoot == null || uploadRoot.isBlank()) {
            throw new IllegalStateException(
                "Biến môi trường PROJECT_STORAGE_ROOT chưa được cấu hình. " +
                "Vui lòng set biến môi trường này trong cấu hình server. " +
                "Tham khảo hướng dẫn trong file SETUP_STORAGE_PATH.md."
            );
        }
        return uploadRoot;
    }
    
    /**
     * Lấy đường dẫn thư mục lưu trữ file đã upload
     * @return Đường dẫn thư mục SavedFiles
     * @throws IllegalStateException nếu biến môi trường PROJECT_STORAGE_ROOT không được set
     */
    public static String getUploadDirectory() {
        return ensureStorageRoot() + File.separator + SAVED_FILES_FOLDER;
    }
    
    /**
     * Lấy đường dẫn gốc từ biến môi trường
     * @return Đường dẫn gốc PROJECT_STORAGE_ROOT
     * @throws IllegalStateException nếu biến môi trường không được set
     */
    public static String getStorageRoot() {
        return ensureStorageRoot();
    }
    
    /**
     * Kiểm tra xem biến môi trường đã được cấu hình chưa
     * @return true nếu đã được cấu hình, false nếu chưa
     */
    public static boolean isConfigured() {
        String uploadRoot = resolveStorageRoot();
        return uploadRoot != null && !uploadRoot.isBlank();
    }
}

