package service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.io.InputStream;

import com.google.gson.Gson;

public class WorkerService {
    private static final String DEFAULT_WORKER_URL = "http://localhost:8081";
    private static final String CONFIG_FILE = "/worker.properties";
    private static String workerBaseUrl;
    private static final Gson gson = new Gson();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        workerBaseUrl = System.getenv("WORKER_URL");
        if (workerBaseUrl == null || workerBaseUrl.isEmpty()) {
            // Try to load from properties file
            try (InputStream is = WorkerService.class.getResourceAsStream(CONFIG_FILE)) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    workerBaseUrl = props.getProperty("worker.url", DEFAULT_WORKER_URL);
                } else {
                    workerBaseUrl = DEFAULT_WORKER_URL;
                }
            } catch (IOException e) {
                System.err.println("[SERVER] Failed to load worker config, using default: " + DEFAULT_WORKER_URL);
                workerBaseUrl = DEFAULT_WORKER_URL;
            }
        }
        System.out.println("[SERVER] Worker URL configured: " + workerBaseUrl);
    }

    public static class ConvertTaskRequest {
        private Integer taskId;
        private String pdfPath;
        private String callbackUrl;

        public ConvertTaskRequest(Integer taskId, String pdfPath, String callbackUrl) {
            this.taskId = taskId;
            this.pdfPath = pdfPath;
            this.callbackUrl = callbackUrl;
        }

        public Integer getTaskId() { return taskId; }
        public String getPdfPath() { return pdfPath; }
        public String getCallbackUrl() { return callbackUrl; }
    }

    public static class ConvertTaskResponse {
        private boolean success;
        private String message;

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    /**
     * Gửi task convert đến Worker
     * @param taskId ID của task
     * @param pdfPath Đường dẫn file PDF
     * @param serverBaseUrl URL của server để callback (optional)
     * @return true nếu gửi thành công
     */
    public static boolean sendConvertTask(Integer taskId, String pdfPath, String serverBaseUrl) {
        try {
            String callbackUrl = null;
            if (serverBaseUrl != null && !serverBaseUrl.isEmpty()) {
                callbackUrl = serverBaseUrl + "/api/worker/callback";
            }

            ConvertTaskRequest request = new ConvertTaskRequest(taskId, pdfPath, callbackUrl);
            String json = gson.toJson(request);

            URL url = new URL(workerBaseUrl + "/api/convert");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("[SERVER] Worker response code: " + responseCode);

            if (responseCode == 202) {
                System.out.println("[SERVER] Task " + taskId + " sent to worker successfully");
                return true;
            } else {
                System.err.println("[SERVER] Worker returned error: " + responseCode);
                return false;
            }

        } catch (Exception e) {
            System.err.println("[SERVER] Failed to send task to worker: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra Worker có đang chạy không
     */
    public static boolean isWorkerAvailable() {
        try {
            URL url = new URL(workerBaseUrl + "/health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getWorkerUrl() {
        return workerBaseUrl;
    }
}

