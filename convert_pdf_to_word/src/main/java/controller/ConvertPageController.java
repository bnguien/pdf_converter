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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareViewData(request);
        request.getRequestDispatcher("convert.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void prepareViewData(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }

        Object fileNameObj = request.getAttribute("fileName");
        Object filePathObj = request.getAttribute("filePath");

        if (fileNameObj == null) {
            fileNameObj = session.getAttribute("uploadedFileName");
            if (fileNameObj != null) {
                request.setAttribute("fileName", fileNameObj);
            }
        }

        if (filePathObj == null) {
            filePathObj = session.getAttribute("uploadedFilePath");
            if (filePathObj != null) {
                request.setAttribute("filePath", filePathObj);
            }
        }

        Integer userId = (session != null) ? (Integer) session.getAttribute("user_id") : null;
        Integer guestTaskId = (session != null) ? (Integer) session.getAttribute("guest_taskId") : null;
        String guestDocxPath = (session != null) ? (String) session.getAttribute("guest_docxPath") : null;

        Task taskInfo = null;
        String taskStatus = null;
        String docxPathFromDb = null;

        if (guestTaskId != null) {
            TaskBO taskBO = new TaskBO();
            if (userId != null) {
                taskInfo = taskBO.getCompletedTaskDetail(guestTaskId, userId);
            } else {
                taskInfo = taskBO.getTaskIfUserNull(guestTaskId);
            }
        }

        if (taskInfo != null) {
            taskStatus = taskInfo.getStatus();
            docxPathFromDb = taskInfo.getDocxPath();
            request.setAttribute("taskDetails", taskInfo);
            if (taskInfo.getPdfName() != null) {
                session.setAttribute("uploadedFileName", taskInfo.getPdfName());
            }
        }

        if (docxPathFromDb != null) {
            session.setAttribute("guest_docxPath", docxPathFromDb);
            guestDocxPath = docxPathFromDb;
        }

        if (taskStatus == null && guestDocxPath != null) {
            taskStatus = "DONE";
        }

        boolean isProcessing = "PROCESSING".equalsIgnoreCase(taskStatus);
        boolean isDone = "DONE".equalsIgnoreCase(taskStatus) && guestDocxPath != null;
        boolean canShowConvertButton = (guestTaskId == null) || (!isProcessing && !isDone);

        request.setAttribute("taskStatus", taskStatus);
        request.setAttribute("isProcessing", isProcessing);
        request.setAttribute("isDone", isDone);
        request.setAttribute("canShowConvertButton", canShowConvertButton);
    }
}

