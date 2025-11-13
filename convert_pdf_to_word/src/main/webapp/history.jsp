<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ page import="java.util.*, model.bo.TaskBO, model.dao.TaskDAO, model.bean.Task, model.enums.TaskStatus" %>

<%
    // Khai báo và lấy dữ liệu Task (Giả định: userId là Integer)
    int userId = (Integer) session.getAttribute("user_id"); 
    TaskDAO dao = new TaskDAO(); 
    List<Task> list = dao.getTasksByUserId(userId);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>PDF Converter History</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="css/styles.css"/>
    <!--Google API-->
    <script type="text/javascript" src="https://apis.google.com/js/api.js"></script>
    <script type="text/javascript" src="https://apis.google.com/js/client.js"></script>
</head>
<body>
    
</body>
<h2>Lịch sử chuyển đổi</h2>

<table border="1">
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
        <tr class="<%= colorClass %>">
            <td>
                <%= t.getId() %>
            </td>
            <td>
                <%= t.getPdfName() %>
            </td>
            <td>
                <%= statusString %>
            </td>
            <td>
                <%= t.getUploadedAt() %>
            </td>
            <td>
                <%= t.getCompletedAt() != null ? t.getCompletedAt() : "..." %>
            </td>
            <td>
                <% if (isDone) { %>
                <form action="<%= request.getContextPath() %>/download" method="GET" style="display:inline-block; margin-right: 5px;">
                    <input type="hidden" name="taskId" value="<%= t.getId() %>">
                    <input type="hidden" name="type" value="pdf">
                    <button type="submit" class="download-btn">
                        <i class="fas fa-download"></i> Download PDF
                    </button>
                </form>

                <form action="<%= request.getContextPath() %>/download" method="GET" style="display:inline-block;">
                    <input type="hidden" name="taskId" value="<%= t.getId() %>">
                    <input type="hidden" name="type" value="docx">
                    <button type="submit" class="download-btn">
                        <i class="fas fa-download"></i> Download DOCX
                    </button>
                </form>
                <% } else { %>
                    <span class="status-<%= statusString.toLowerCase() %>"><%= statusString %></span>
                <% } %>
            </td>
        </tr>
    <% } %>
</table>

<div class="text-center mt-3">
    <button type="button" class="btn btn-primary w-100 py-2 fw-semibold" onclick="window.location.href='index.jsp'">
        Quay lại 
    </button>
</div>

<script>
    setInterval(function () {
        if (!document.hidden) {
            fetch(window.location.href, { cache: "no-store" })
                .then(res => res.text())
                .then(html => {
                    const parser = new DOMParser();
                    const newDoc = parser.parseFromString(html, "text/html");
                    const newTable = newDoc.querySelector("table");
                    const oldTable = document.querySelector("table");
                    if (newTable && oldTable) oldTable.innerHTML = newTable.innerHTML;
                })
                .catch(err => console.error("Reload error:", err));
        }
    }, 4000);
</script>
</html>