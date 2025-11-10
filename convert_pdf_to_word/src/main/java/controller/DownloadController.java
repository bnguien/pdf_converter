package controller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Task;
import model.bo.TaskBO;
import util.Utils;

@WebServlet("/download")
public class DownloadController extends HttpServlet {
     private static final String MIME_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
     private static final TaskBO taskBO = new TaskBO();

     @Override
     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          request.setCharacterEncoding("UTF-8");
          response.setCharacterEncoding("UTF-8");

          HttpSession session = request.getSession(false);
          if (session == null || session.getAttribute("user_id") == null) {
               response.sendRedirect(request.getContextPath() + "/login.jsp?error=not_logged_in");
               return;
          }

          try {
               int userId = (Integer)session.getAttribute("user_id");
               System.out.println("[DEBUG] Request parameter user_id " + userId);

               int taskId = (request.getParameter("taskId") != null) ? Integer.parseInt(request.getParameter("taskId")) : -1;
               System.out.println("[DEBUG] Request parameter task_id " + taskId);
               
               if (taskId == -1) {
                    System.out.println("[DEBUG] taskId is null or empty!");
                    response.sendRedirect(request.getContextPath() + "/history.jsp?error=no_taskId");
                    return;
               }

               String type = request.getParameter("type");
               System.out.println("[DEBUG] Request parameter type " + type);

               if (type == null || type.isBlank()) {
                    System.out.println("[DEBUG] type is null or empty!");
                    response.sendRedirect(request.getContextPath() + "/history.jsp?error=no_type");
                    return;
               }

               try {
                    Task task = taskBO.getCompletedTaskDetail(taskId, userId);

                    if (task == null) {
                         System.out.println("[DEBUG] No task found or not owned by user: " + taskId);
                         response.sendRedirect(request.getContextPath() + "/history.jsp?error=no_task_or_permission");
                         return;
                    }

                    if (!("pdf".equalsIgnoreCase(type) || "docx".equalsIgnoreCase(type))) {
                         System.out.println("[DEBUG] Invalid type requested: " + type);
                         response.sendRedirect(request.getContextPath() + "/history.jsp?error=invalid_type");
                         return;
                    }

                    String filePath = ("docx".equalsIgnoreCase(type)) ? task.getDocxPath() : task.getPdfPath();
                    if (filePath == null || filePath.isBlank()) {
                         System.out.println("[DEBUG] File path is empty for task " + taskId + " type=" + type);
                         response.sendRedirect(request.getContextPath() + "/history.jsp?error=no_file_path");
                         return;
                    }

                    Path sourcePath = Paths.get(filePath);
                    if (!Files.exists(sourcePath) || !Files.isReadable(sourcePath)) {
                         System.out.println("[DEBUG] cannot find file " + type + " with filepath " + filePath);
                         response.sendRedirect(request.getContextPath() + "/history.jsp?error=no_existed_file");
                         return;
                    }

                    // Choose content type and download name
                    String fileName;
                    String contentType;
                    if ("pdf".equalsIgnoreCase(type)) {
                         fileName = task.getPdfName();
                         contentType = "application/pdf";
                    } else {
                         // prefer docx path/file name if available
                         fileName = task.getPdfName();
                         if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                              fileName = fileName.substring(0, fileName.length() - 4) + ".docx";
                         } else {
                              fileName = fileName + ".docx";
                         }
                         contentType = MIME_DOCX;
                    }

                    long fileSize = Files.size(sourcePath);

                    // Set headers to force Save As dialog and handle UTF-8 filenames
                    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
                    String asciiFileName = Utils.sanitizeFileName(fileName);
                    response.setContentType(contentType);
                    response.setContentLengthLong(fileSize);
                    // Provide both an ASCII fallback filename and the RFC5987 UTF-8 filename*
                    response.setHeader("Content-Disposition",
                              "attachment; filename=\"" + asciiFileName.replaceAll("\"","\'") + "\"; filename*=UTF-8''" + encodedFileName);
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");

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

                    return;
               } catch (IOException ex) {
                    System.err.println("[DEBUG] Download IO Error: " + ex.getMessage());
                    response.sendRedirect(request.getContextPath() + "/history.jsp?error=download_io_error");
                    return;
               }
               
          } catch(Exception e) {
               System.err.println("[DEBUG] " + e.getMessage());
               response.sendRedirect(request.getContextPath() + "/history.jsp?error=download_failed");
          }
     }

     @Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          doGet(request, response);
     }
}
