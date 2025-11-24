package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Task;
import model.bo.TaskBO;

@WebServlet("/convert-page")
public class ConvertPageController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final TaskBO taskBO = new TaskBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        prepareViewData(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void prepareViewData(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(true);

        Integer userId = (Integer) session.getAttribute("user_id");
        
        Integer guestTaskId = (Integer) session.getAttribute("guest_taskId");
        String uploadedFilePath = (String) session.getAttribute("uploadedFilePath");
        String uploadedFileName = (String) session.getAttribute("uploadedFileName");
        String guestDocxPath = (String) session.getAttribute("guest_docxPath");

        if (guestTaskId == null) {
            if (uploadedFilePath == null || uploadedFilePath.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=no_file_uploaded");
                return;
            }

            // Người dùng đã upload file nhưng chưa tạo task convert.
            request.setAttribute("taskStatus", "PENDING");
            request.setAttribute("isProcessing", false);
            request.setAttribute("isDone", false);
            request.setAttribute("canShowConvertButton", true);
            request.setAttribute("filePath", uploadedFilePath);
            request.setAttribute("fileName", uploadedFileName);
            request.getRequestDispatcher("/convert.jsp").forward(request, response);
            return;
        }
        
        Task taskInfo;
        String taskStatus = "UNKNOWN";
        if (userId != null) {
            taskInfo = this.taskBO.getCompletedTaskDetail(guestTaskId, userId); 
        } else {
            taskInfo = this.taskBO.getTaskIfUserNull(guestTaskId);
        }

        if (taskInfo != null) {
            taskStatus = taskInfo.getStatus();
            String docxPathFromDb = taskInfo.getDocxPath();
            if (taskStatus.equals("DONE") && docxPathFromDb != null) {
                session.setAttribute("guest_docxPath", docxPathFromDb); 
            }
            
            request.setAttribute("taskDetails", taskInfo);
        }

        if (taskStatus.equals("UNKNOWN") && guestDocxPath != null) {
            taskStatus = "DONE";
        }

        boolean isProcessing = "PROCESSING".equalsIgnoreCase(taskStatus) || "PENDING".equalsIgnoreCase(taskStatus);
        boolean isDone = "DONE".equalsIgnoreCase(taskStatus);
        boolean canShowConvertButton = !isProcessing && !isDone;
        if (taskStatus.equals("UNKNOWN") || taskStatus.equals("FAILED")) {
            canShowConvertButton = true;
        }

        request.setAttribute("taskStatus", taskStatus);
        request.setAttribute("isProcessing", isProcessing);
        request.setAttribute("isDone", isDone);
        request.setAttribute("canShowConvertButton", canShowConvertButton);
        request.getRequestDispatcher("/convert.jsp").forward(request, response);
    }
}

