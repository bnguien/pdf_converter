package controller;

import java.io.IOException;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bean.Task;
import model.bo.TaskBO;

@WebServlet("/api/status")
public class ApiStatusController extends HttpServlet {
     private final TaskBO taskBO = new TaskBO();
     private static final Gson GSON = new Gson();
     
     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");

          Integer userId = (Integer) request.getSession().getAttribute("user_id");
          String taskIdStr = request.getParameter("taskId");

          if (taskIdStr == null || taskIdStr.isEmpty()) {
               response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               response.getWriter().write("{\"status\": \"FAILED\", \"message\": \"Missing Task ID\"}");
               return;
          }
          
          int taskId;
          try {
               taskId = Integer.parseInt(taskIdStr);
          } catch (NumberFormatException e) {
               response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               response.getWriter().write("{\"status\": \"FAILED\", \"message\": \"Invalid Task ID\"}");
               return;
          }

          Task task = null;
          if (userId != null) {
               task = taskBO.getCompletedTaskDetail(taskId, userId);
          } else {
               task = taskBO.getTaskIfUserNull(taskId);
          }
          
          if (task != null) {
               String currentStatus = task.getStatus();
               String docxPath = task.getDocxPath();

               if (!currentStatus.equals("DONE")) {
                    docxPath = null;
               }

               TaskStatusResponse jsonResponse = new TaskStatusResponse(currentStatus, docxPath);
               //Gửi phản hồi thành công (HTTP 200)
               response.getWriter().write(GSON.toJson(jsonResponse));
          } else {
               response.setStatus(HttpServletResponse.SC_NOT_FOUND);
               response.getWriter().write("{\"status\": \"FAILED\", \"message\": \"Task not found\"}");
          }
     }

     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
          
     }
}

class TaskStatusResponse {
     private String status;
     private String docxPath;

     public TaskStatusResponse(String status, String docxPath) {
          this.status = status;
          this.docxPath = docxPath;
     }

     public void setStatus(String status) {
          this.status = status;
     }
     public void setDocxPath(String docxPath) {
          this.docxPath = docxPath;
     }
     public String getStatus() {
          return status;
     }
     public String getDocxPath() {
          return docxPath;
     }
}
