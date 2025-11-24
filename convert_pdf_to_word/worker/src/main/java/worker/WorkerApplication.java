package worker;

import com.google.gson.Gson;
import spark.Spark;
import worker.api.ConvertTaskRequest;
import worker.api.ConvertTaskResponse;
import worker.service.ConversionService;
import worker.util.DBConnection;

import static spark.Spark.*;

public class WorkerApplication {
    private static final int DEFAULT_PORT = 8081;
    private static final Gson gson = new Gson();
    private static ConversionService conversionService;

    public static void main(String[] args) {
        // Get port from environment or use default
        int port = DEFAULT_PORT;
        if (System.getenv("WORKER_PORT") != null) {
            try {
                port = Integer.parseInt(System.getenv("WORKER_PORT"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid WORKER_PORT, using default: " + DEFAULT_PORT);
            }
        }

        port(port);
        
        // Initialize services
        conversionService = new ConversionService();
        
        // CORS headers
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        // Health check endpoint
        get("/health", (req, res) -> {
            res.type("application/json");
            return gson.toJson(new HealthResponse("OK", "Worker is running"));
        });

        // Convert task endpoint
        post("/api/convert", (req, res) -> {
            res.type("application/json");
            try {
                ConvertTaskRequest request = gson.fromJson(req.body(), ConvertTaskRequest.class);
                
                System.out.println("[WORKER] Received convert task:");
                System.out.println("  Task ID: " + request.getTaskId());
                System.out.println("  PDF Path: " + request.getPdfPath());
                
                // Validate request
                if (request.getTaskId() == null || request.getPdfPath() == null) {
                    res.status(400);
                    return gson.toJson(new ConvertTaskResponse(false, "Missing taskId or pdfPath"));
                }

                // Process conversion asynchronously
                conversionService.convertAsync(request.getTaskId(), request.getPdfPath(), 
                    request.getCallbackUrl());

                res.status(202); // Accepted
                return gson.toJson(new ConvertTaskResponse(true, "Task accepted and processing"));
                
            } catch (Exception e) {
                System.err.println("[WORKER] Error processing request: " + e.getMessage());
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new ConvertTaskResponse(false, "Internal server error: " + e.getMessage()));
            }
        });

        // Get worker status
        get("/api/status", (req, res) -> {
            res.type("application/json");
            return gson.toJson(new WorkerStatusResponse(
                conversionService.getActiveTasks(),
                conversionService.getCompletedTasks(),
                conversionService.getFailedTasks()
            ));
        });

        System.out.println("==========================================");
        System.out.println("PDF Converter Worker started!");
        System.out.println("Port: " + port);
        System.out.println("Health check: http://localhost:" + port + "/health");
        System.out.println("Convert API: http://localhost:" + port + "/api/convert");
        System.out.println("==========================================");
    }

    // Response classes
    static class HealthResponse {
        String status;
        String message;
        
        HealthResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    static class WorkerStatusResponse {
        int activeTasks;
        int completedTasks;
        int failedTasks;

        WorkerStatusResponse(int activeTasks, int completedTasks, int failedTasks) {
            this.activeTasks = activeTasks;
            this.completedTasks = completedTasks;
            this.failedTasks = failedTasks;
        }
    }
}

