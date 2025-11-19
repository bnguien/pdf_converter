<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>

<%
    Object fileNameObj = request.getAttribute("fileName");
    Object filePathObj = request.getAttribute("filePath");
    if (filePathObj == null) filePathObj = session.getAttribute("uploadedFilePath");
    if (fileNameObj == null) fileNameObj = session.getAttribute("uploadedFileName");

    String fileName = null;
    String fileUrl = null;

    if (fileNameObj != null) {
        fileName = fileNameObj.toString();
    }

    if (filePathObj != null) {
        String absolutePath = filePathObj.toString().replace("\\", "/");
        String fileNameOnly = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);

        fileUrl = request.getScheme() + "://" 
                + request.getServerName() + ":" 
                + request.getServerPort()
                + request.getContextPath()
                + "/upload/" + fileNameOnly;
    }

    Integer userId = (Integer) session.getAttribute("user_id");

    Integer guestTaskId = (Integer) session.getAttribute("guest_taskId");
    String taskStatus = (String) request.getAttribute("taskStatus");
    Boolean processingAttr = (Boolean) request.getAttribute("isProcessing");
    Boolean doneAttr = (Boolean) request.getAttribute("isDone");
    Boolean canConvertAttr = (Boolean) request.getAttribute("canShowConvertButton");

    boolean isProcessing = processingAttr != null && processingAttr;
    boolean isDone = doneAttr != null && doneAttr;
    boolean canShowConvertButton = canConvertAttr == null ? true : canConvertAttr;
%>

<%@ include file="/header.jsp" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/convert.css">

<div class="main">

    <% if (fileNameObj != null && filePathObj != null) { %>

        <p class="file-label">
            <strong>Tên file:</strong> <%= fileName %>
        </p>

        <div class="pdf-preview">
            <canvas id="pdfThumb" width="200"></canvas>
        </div>

        <% if (taskStatus != null) { %>
            <div class="status-box">
                <span class="status-label">Trạng thái:</span>
                <span class="status-badge <%= isDone ? "done" : (isProcessing ? "processing" : "pending") %>">
                    <%= taskStatus %>
                </span>
                <% if (isProcessing) { %>
                    <p class="status-note">Hệ thống đang xử lý file của bạn...</p>
                <% } else if (isDone) { %>
                    <p class="status-note">Tệp đã sẵn sàng để tải xuống.</p>
                <% } %>
            </div>
        <% } %>

        <% if (canShowConvertButton) { %> 
            <form action="convert" method="POST" class="convert-form">
                <input type="hidden" name="filePath" value="<%= filePathObj %>">
                <button type="submit" class="convert-btn">Convert sang Word</button>
            </form>
        <% } %>

        <% if (isProcessing) { %>
            <div class="processing-hint">
                <p>Vui lòng chờ trong giây lát. Trang sẽ tự động cập nhật khi hoàn tất.</p>
            </div>
        <% } %>

        <% if (isDone) { %>
            <div class="download-area">
                <form action="<%= request.getContextPath() %>/download" method="GET">
                    <input type="hidden" name="taskId" value="<%= guestTaskId %>">
                    <input type="hidden" name="type" value="pdf">
                    <button type="submit" class="download-btn">
                        <i class="fas fa-download"></i> Tải file PDF
                    </button>
                </form>
                <form action="<%= request.getContextPath() %>/download" method="GET">
                    <input type="hidden" name="taskId" value="<%= guestTaskId %>">
                    <input type="hidden" name="type" value="docx">
                    <button type="submit" class="download-btn">
                        <i class="fas fa-download"></i> Tải file DOCX
                    </button>
                </form>
            </div>

            <% if (userId != null) { %>
            <div class="history-link">
                <a href="<%= request.getContextPath() %>/history.jsp">Xem lịch sử</a>
            </div>
            <% } %>
        <% } %>

        <div class="action">
            <div class="text-center mt-3" style="margin-top: 20px;">
                <button type="button" class="btn-back" onclick="window.location.href='index.jsp'">
                    Quay lại
                </button>
            </div>
        </div>

    <% } else { %>

        <p class="error-text">
            Không có tệp nào được tải lên.<br>
            Vui lòng quay lại trang chính và thử lại.
        </p>

    <% } %>

</div>

<%@ include file="/footer.jsp" %>

<script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.min.js"></script>

<script>
    const pdfPath = "<%= fileUrl %>";
</script>

<script src="<%= request.getContextPath() %>/js/pdf-preview.js"></script>
<script src="<%= request.getContextPath() %>/js/convert-page.js"></script>

<% if (isProcessing) { %>
<script>
    setTimeout(function () {
        window.location.href = "<%= request.getContextPath() %>/convert-page";
    }, 4000);
</script>
<% } %>