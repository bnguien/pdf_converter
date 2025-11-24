package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Task;
import model.bo.TaskBO;

@WebServlet("/serve-file")
public class ServeFileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final TaskBO taskBO = new TaskBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("user_id") : null;
        Integer guestTaskIdInSession = (session != null) ? (Integer) session.getAttribute("guest_taskId") : null;

        try {
            // Lấy taskId từ parameter
            String taskIdStr = request.getParameter("taskId");
            if (taskIdStr == null || taskIdStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing taskId parameter");
                return;
            }

            int taskId;
            try {
                taskId = Integer.parseInt(taskIdStr);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid taskId");
                return;
            }

            // Lấy type từ parameter (pdf hoặc docx)
            String type = request.getParameter("type");
            if (type == null || type.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing type parameter");
                return;
            }

            if (!("pdf".equalsIgnoreCase(type) || "docx".equalsIgnoreCase(type))) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid type. Must be 'pdf' or 'docx'");
                return;
            }

            // Lấy task từ database
            Task task = null;
            if (userId != null) {
                task = taskBO.getCompletedTaskDetail(taskId, userId);
            } else {
                // Guest user - kiểm tra permission
                if (guestTaskIdInSession == null || guestTaskIdInSession.intValue() != taskId) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "No permission to access this file");
                    return;
                }
                task = taskBO.getTaskIfUserNull(taskId);
            }

            if (task == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found");
                return;
            }

            // Lấy file path
            String filePath = ("docx".equalsIgnoreCase(type)) ? task.getDocxPath() : task.getPdfPath();
            if (filePath == null || filePath.isBlank()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File path not found for task " + taskId);
                return;
            }

            Path sourcePath = Paths.get(filePath);
            if (!Files.exists(sourcePath) || !Files.isReadable(sourcePath)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + filePath);
                return;
            }

            // Xác định content type
            String contentType;
            if ("pdf".equalsIgnoreCase(type)) {
                contentType = "application/pdf";
            } else {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }

            // Set headers - inline để preview, không download
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline"); // inline để preview
            response.setHeader("Cache-Control", "public, max-age=3600");

            // Stream file
            try (InputStream in = Files.newInputStream(sourcePath);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }

        } catch (Exception e) {
            System.err.println("[DEBUG] ServeFileController error: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving file");
        }
    }
}

