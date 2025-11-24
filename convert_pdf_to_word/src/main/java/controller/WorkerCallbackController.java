package controller;

import java.io.BufferedReader;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import model.bo.TaskBO;

@WebServlet("/api/worker/callback")
public class WorkerCallbackController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    static class CallbackRequest {
        Integer taskId;
        String status;
        String docxPath;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON body
            StringBuilder jsonBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBody.append(line);
                }
            }

            CallbackRequest callback = gson.fromJson(jsonBody.toString(), CallbackRequest.class);
            
            System.out.println("[SERVER] Received callback from worker:");
            System.out.println("  Task ID: " + callback.taskId);
            System.out.println("  Status: " + callback.status);
            System.out.println("  DOCX Path: " + callback.docxPath);

            if (callback.taskId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\":false,\"message\":\"Missing taskId\"}");
                return;
            }

            // Update task in database
            TaskBO taskBO = new TaskBO();
            taskBO.updateTask(callback.taskId, callback.status, callback.docxPath);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\":true,\"message\":\"Callback processed\"}");

        } catch (Exception e) {
            System.err.println("[SERVER] Error processing worker callback: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Internal error\"}");
        }
    }
}

