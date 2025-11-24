package worker.service;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import worker.dao.TaskDAO;
import worker.util.PdfConvertionHelper;

public class ConversionService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(
        Math.max(4, Runtime.getRuntime().availableProcessors() * 2)
    );
    
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger failedTasks = new AtomicInteger(0);
    
    private final TaskDAO taskDAO = new TaskDAO();
    private final Gson gson = new Gson();

    public void convertAsync(Integer taskId, String pdfPath, String callbackUrl) {
        activeTasks.incrementAndGet();
        
        executor.submit(() -> {
            try {
                System.out.println("[WORKER] Starting conversion for task " + taskId);
                System.out.println("[WORKER] PDF Path: " + pdfPath);
                
                // Check if file exists
                File pdfFile = new File(pdfPath);
                if (!pdfFile.exists()) {
                    throw new Exception("PDF file not found: " + pdfPath);
                }

                // Convert PDF to DOCX
                PdfConvertionHelper.convertPdfToDoc(pdfPath);
                
                String docxPath = pdfPath.replace(".pdf", ".docx");
                File docxFile = new File(docxPath);
                
                if (!docxFile.exists()) {
                    throw new Exception("Conversion failed: DOCX file not created");
                }

                System.out.println("[WORKER] Conversion completed for task " + taskId);
                System.out.println("[WORKER] DOCX Path: " + docxPath);

                // Update database
                taskDAO.updateTaskStatus(taskId, "DONE", docxPath);
                
                // Callback to server if provided
                if (callbackUrl != null && !callbackUrl.isEmpty()) {
                    sendCallback(callbackUrl, taskId, "DONE", docxPath);
                }

                activeTasks.decrementAndGet();
                completedTasks.incrementAndGet();
                
            } catch (Exception e) {
                System.err.println("[WORKER] Conversion failed for task " + taskId + ": " + e.getMessage());
                e.printStackTrace();
                
                try {
                    // Update database with FAILED status
                    taskDAO.updateTaskStatus(taskId, "FAILED", null);
                    
                    // Callback to server if provided
                    if (callbackUrl != null && !callbackUrl.isEmpty()) {
                        sendCallback(callbackUrl, taskId, "FAILED", null);
                    }
                } catch (Exception updateEx) {
                    System.err.println("[WORKER] Failed to update task status: " + updateEx.getMessage());
                }
                
                activeTasks.decrementAndGet();
                failedTasks.incrementAndGet();
            }
        });
    }

    private void sendCallback(String callbackUrl, Integer taskId, String status, String docxPath) {
        try {
            URL url = new URL(callbackUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            CallbackRequest callback = new CallbackRequest(taskId, status, docxPath);
            String json = gson.toJson(callback);
            
            conn.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            
            int responseCode = conn.getResponseCode();
            System.out.println("[WORKER] Callback response code: " + responseCode);
            
            conn.disconnect();
        } catch (Exception e) {
            System.err.println("[WORKER] Failed to send callback: " + e.getMessage());
        }
    }

    public int getActiveTasks() {
        return activeTasks.get();
    }

    public int getCompletedTasks() {
        return completedTasks.get();
    }

    public int getFailedTasks() {
        return failedTasks.get();
    }

    // Callback request class
    static class CallbackRequest {
        Integer taskId;
        String status;
        String docxPath;

        CallbackRequest(Integer taskId, String status, String docxPath) {
            this.taskId = taskId;
            this.status = status;
            this.docxPath = docxPath;
        }
    }
}

