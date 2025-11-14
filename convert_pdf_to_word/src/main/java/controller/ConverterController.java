package controller;

import java.io.IOException;
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

@WebServlet("/convert")
@MultipartConfig
public class ConverterController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("========== [CONVERTER] START ==========");

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);
        System.out.println("[DEBUG] Session ID = " + session.getId());
        System.out.println("[DEBUG] Session attributes:");
        System.out.println("   uploadedFilePath = " + session.getAttribute("uploadedFilePath"));
        System.out.println("   uploadedFileName = " + session.getAttribute("uploadedFileName"));
        System.out.println("   user_id = " + session.getAttribute("user_id"));

        Integer userId = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        String filePathInServer = request.getParameter("filePath");
        System.out.println("[DEBUG] request.getParameter(filePath) = " + filePathInServer);

        if (filePathInServer == null || filePathInServer.isBlank()) {
            filePathInServer = (String) session.getAttribute("uploadedFilePath");
            System.out.println("[DEBUG] Using session uploadedFilePath = " + filePathInServer);
        }

        if (filePathInServer == null || filePathInServer.isBlank()) {
            System.out.println("[DEBUG] filePath is STILL NULL — cannot convert!");
            response.sendRedirect("convert.jsp?error=no_file_path");
            System.out.println("========== [CONVERTER] END (FAIL) ==========");
            return;
        }

        File file = new File(filePathInServer);
        String fileNameUserUpload = file.getName();

        System.out.println("[DEBUG] File absolute path = " + file.getAbsolutePath());
        System.out.println("[DEBUG] File exists? " + file.exists());
        System.out.println("[DEBUG] File size = " + (file.exists() ? file.length() : "N/A"));

        final String filePathFinal = filePathInServer;

        try {
            TaskBO taskBO = new TaskBO();
            Task task = new Task();

            task.setUserId(userId);
            System.out.println("[DEBUG] task.setUserId = " + userId);

            task.setPdfName(fileNameUserUpload);
            System.out.println("[DEBUG] task.setPdfName = " + fileNameUserUpload);

            task.setPdfPath(filePathInServer);
            System.out.println("[DEBUG] task.setPdfPath = " + filePathInServer);

            task.setStatus("PROCESSING");
            System.out.println("[DEBUG] task.setStatus = PROCESSING");

            int taskId = taskBO.addTask(task);
            System.out.println("[DEBUG] Task created with ID = " + taskId);

            final int taskIdFinal = taskId;

            System.out.println("[DEBUG] Starting convertAsync for: " + filePathFinal);

            PdfConverter.convertAsync(filePathFinal, (outputPath) -> {
                System.out.println("[DEBUG] convertAsync callback fired.");
                System.out.println("[DEBUG] outputPath = " + outputPath);
                System.out.println("[DEBUG] Updating taskId = " + taskIdFinal);

                try {
                    taskBO.updateTask(taskIdFinal, "DONE", outputPath);
                    System.out.println("[DEBUG] updateTask DONE");
                } catch (Exception e) {
                    System.out.println("[ERROR] updateTask FAILED");
                    e.printStackTrace();
                }
            });

            if (userId == null) {
                System.out.println("[DEBUG] Guest mode → redirect convert.jsp");
                response.sendRedirect(
                    "convert.jsp?status=done&taskId=" + taskId +
                    "&pdfName=" + fileNameUserUpload
                );
            } else {
                System.out.println("[DEBUG] User logged in → redirect history.jsp");
                response.sendRedirect("history.jsp?message=conversion_started");
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Conversion failed:");
            e.printStackTrace();
            response.sendRedirect("convert.jsp?error=conversion_failed");
        }

        System.out.println("========== [CONVERTER] END ==========");
    }
}
