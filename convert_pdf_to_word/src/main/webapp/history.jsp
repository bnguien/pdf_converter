<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page import="java.util.*, model.bo.TaskBO, model.dao.TaskDAO, model.bean.Task, model.enums.TaskStatus" %>

<%
    int userId = (Integer) session.getAttribute("user_id");
    TaskDAO dao = new TaskDAO();
    List<Task> list = dao.getTasksByUserId(userId);
%>

<%@ include file="/header.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/history.css">

<div class="main">
    <table border="1" style="width: 100%; margin-top: 15px; border-collapse: collapse;">
        <tr>
            <th>ID</th>
            <th>Tên file</th>
            <th>Trạng thái</th>
            <th>Thời gian tải lên</th>
            <th>Hoàn thành</th>
            <th>Action</th>
        </tr>

        <% for (Task t : list) { %>
            <% 
                String statusString = t.getStatus();
                boolean isDone = statusString.equals(TaskStatus.DONE.name());
                String colorClass = isDone ? "status-done" : "status-processing";
            %>

            <tr>
                <td><%= t.getId() %></td>
                <td><%= t.getPdfName() %></td>
                <td class="status-cell <%= statusString.toLowerCase() %>"><%= statusString %></td>
                <td><%= t.getUploadedAt() %></td>
                <td><%= t.getCompletedAt() != null ? t.getCompletedAt() : "..." %></td>

                <td>
                    <% if (isDone) { %>

                    <form action="<%= request.getContextPath() %>/download" method="GET" style="display:inline-block; margin-right: 5px;">
                        <input type="hidden" name="taskId" value="<%= t.getId() %>">
                        <input type="hidden" name="type" value="pdf">
                        <button type="submit" class="download-btn">
                            <i class="fas fa-download"></i> PDF
                        </button>
                    </form>

                    <form action="<%= request.getContextPath() %>/download" method="GET" style="display:inline-block;">
                        <input type="hidden" name="taskId" value="<%= t.getId() %>">
                        <input type="hidden" name="type" value="docx">
                        <button type="submit" class="download-btn">
                            <i class="fas fa-download"></i> DOCX
                        </button>
                    </form>

                    <% } else { %>
                        <span class="status-<%= statusString.toLowerCase() %>"><%= statusString %></span>
                    <% } %>
                </td>
            </tr>

        <% } %>

    </table>

    <div class="text-center mt-3" style="margin-top: 20px;">
        <button type="button" class="btn btn-primary w-100 py-2 fw-semibold"
                onclick="window.location.href='index.jsp'">
            Quay lại
        </button>
    </div>

</div> 
<script src="<%= request.getContextPath() %>/js/history-refresh.js"></script>
<%@ include file="/footer.jsp" %>
