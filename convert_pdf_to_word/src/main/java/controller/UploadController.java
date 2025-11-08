package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.bean.Task;
import model.bo.TaskBO;
import util.Validator;

@WebServlet("/upload")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 20,       
    maxRequestSize = 1024 * 1024 * 50     
)
public class UploadController extends HttpServlet {
     private final TaskBO taskBO = new TaskBO();

     @Override
     protected void doGet(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
          // use context-aware redirect to avoid relative-path ambiguity
          response.sendRedirect(request.getContextPath() + "/index.jsp");
     }

     @Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
          
          Integer userId = (Integer) request.getSession().getAttribute("user_id");
          if (userId == null) {
               response.sendRedirect(request.getContextPath() + "/login.jsp"); 
               return;
          }

          try {
               Part filePart = request.getPart("pdfFile"); 

               if (filePart == null || filePart.getSize() == 0) {
                    response.sendRedirect(request.getContextPath() + "/index.jsp?status=error&msg=no_file_selected");
                    return;
               }

               String contentType = filePart.getContentType();
               String fileName = filePart.getSubmittedFileName();

               if (Validator.isInvalidPDFFile(contentType, fileName)) {
                    response.sendRedirect(request.getContextPath() + "/index.jsp?status=error&msg=invalid_file_type");
                    return;
               } 

               String applicationRootPath = request.getServletContext().getRealPath("");
               String UPLOAD_BASE_DIR = applicationRootPath + File.separator + "SavedFiles";
               
               try (InputStream contentFile = filePart.getInputStream()) {
                    
                    Task createdTask = taskBO.processAndSaveTask(userId, fileName, contentType, contentFile, UPLOAD_BASE_DIR);

                    if (createdTask != null) {
                         String target = request.getContextPath() + "/index.jsp?upload=success&taskId=" + createdTask.getId() + "&pdfName=" + createdTask.getPdfName();
                         getServletContext().log("UploadController: created task id=" + createdTask.getId() + ", redirecting to " + target);
                         response.sendRedirect(target); 
                    } else {
                         String target = request.getContextPath() + "/index.jsp?status=error&msg=db_save_failed";
                         getServletContext().log("UploadController: createTask returned null, redirecting to " + target);
                         response.sendRedirect(target); 
                    }
               } 
               
          } catch(Exception ex) {
               ex.printStackTrace();
               String target = request.getContextPath() + "/index.jsp?status=error&msg=system_io_error";
               getServletContext().log("UploadController: FATAL EXCEPTION, redirecting to " + target);
               
               if (!response.isCommitted()) {
                    response.sendRedirect(target);
               }
          }
     }
}
