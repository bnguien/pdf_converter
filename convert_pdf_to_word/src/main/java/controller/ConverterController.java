package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.bean.Task;
import model.bo.PdfConverter;
import model.bo.TaskBO;
import util.Utils;

@WebServlet("/convert")
@MultipartConfig
public class ConverterController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String filePathInServer = request.getParameter("filePath");
        System.out.println("[DEBUG] Request parameter filePath = " + filePathInServer);

        if (filePathInServer == null || filePathInServer.isBlank()) {
            System.out.println("[DEBUG] filePathInServer is null or empty!");
            response.sendRedirect("convert.jsp?error=no_file_path");
            return;
        }

        File file = new File(filePathInServer);
        String fileNameUserUpload = file.getName();
        String fileNameInServer = fileNameUserUpload;

        System.out.println("[DEBUG] File from request: " + file.getAbsolutePath());
        System.out.println("[DEBUG] File exists? " + file.exists());
        System.out.println("[DEBUG] File size: " + (file.exists() ? file.length() + " bytes" : "N/A"));

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("user_id") : null;
        String username = (session != null) ? (String) session.getAttribute("user_username") : null;
        if (userId == null || username == null) {
            response.sendRedirect("login.jsp?error=not_logged_in");
            return;
        }

        final String filePathFinal = filePathInServer;
        final String fileNameFinal = fileNameInServer;
        final String fileNameUserFinal = fileNameUserUpload;

        try {
            TaskBO taskBO = new TaskBO();
            Task task = new Task();
            task.setUserId(userId);
            task.setPdfName(fileNameUserUpload);
            task.setPdfPath(filePathInServer);
            task.setStatus("PROCESSING");

            int taskId = taskBO.addTask(task);
            final int taskIdFinal = taskId;

            System.out.println("[DEBUG] Starting async conversion for: " + filePathFinal);
            System.out.println("[DEBUG] Exists before convert? " + Files.exists(Path.of(filePathFinal)));

            PdfConverter.convertAsync(filePathFinal, (outputPath) -> {
                System.out.println("[DEBUG] filePathFinal in callback: " + filePathFinal);
                System.out.println("[DEBUG] file exists in callback? " + Files.exists(Path.of(filePathFinal)));
                try {
                    System.out.println("[DEBUG] taskIdFinal (saved to DB): " + taskIdFinal);
                    System.out.println("[DEBUG] Calling updateTask()...");
                    taskBO.updateTask(taskIdFinal, "DONE", outputPath);
                    System.out.println("[DEBUG] updateTask() finished");
                    System.out.println("[DEBUG] Calling saveHistory()...");
                    taskBO.saveHistory(username, fileNameUserFinal, fileNameFinal);
                    System.out.println("[DEBUG] saveHistory() finished");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            response.sendRedirect("history.jsp?message=conversion_started");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("convert.jsp?error=conversion_failed");
        }
    }
}
