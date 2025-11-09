<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.*, model.bo.TaskBO, model.dao.TaskDAO, model.bean.Task" %>
        <% int userId=(Integer) session.getAttribute("user_id"); TaskDAO dao=new TaskDAO(); List<Task> list =
            dao.getTasksByUserId(userId);
            %>

            <h2>Lịch sử chuyển đổi</h2>

            <table border="1">
                <tr>
                    <th>ID</th>
                    <th>Tên file</th>
                    <th>Trạng thái</th>
                    <th>Thời gian tải lên</th>
                    <th>Hoàn thành</th>
                    <th>Action</th> <%-- Cho phần download file, khi trạng thái là DONE thì sẽ hiển thị nút download --%>
                </tr>
                <% for (Task t : list) { %>
                    <% String status=t.getStatus(); String colorClass="DONE" .equals(status) ? "status-done"
                        : "status-processing" ; %>
                        <tr class="<%= colorClass %>">
                            <td>
                                <%= t.getId() %>
                            </td>
                            <td>
                                <%= t.getPdfName() %>
                            </td>
                            <td>
                                <%= status %>
                            </td>
                            <td>
                                <%= t.getUploadedAt() %>
                            </td>
                            <td>
                                <%= t.getCompletedAt() !=null ? t.getCompletedAt() : "..." %>
                            </td>

                        </tr>
                        <% } %>
            </table>
            <div class="text-center mt-3">
                <button type="button" class="btn btn-primary w-100 py-2 fw-semibold"
                    onclick="window.location.href='index.jsp'">
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