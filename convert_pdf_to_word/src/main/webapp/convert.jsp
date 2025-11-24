<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>

<%
    // Lấy thông tin từ session và request
    Integer userId = (Integer) session.getAttribute("user_id");
    Integer guestTaskId = (Integer) session.getAttribute("guest_taskId");
    
    Object fileNameObj = request.getAttribute("fileName");
    Object filePathObj = request.getAttribute("filePath");
    if (filePathObj == null) filePathObj = session.getAttribute("uploadedFilePath");
    if (fileNameObj == null) fileNameObj = session.getAttribute("uploadedFileName");

    String fileName = null;
    String pdfServeUrl = null;

    if (fileNameObj != null) {
        fileName = fileNameObj.toString();
    }

    // Tạo URL để serve file PDF cho thumbnail
    // Nếu có taskId, dùng /serve-file với taskId
    // Nếu chưa có taskId (mới upload), dùng /upload/ với fileName
    if (filePathObj != null) {
        if (guestTaskId != null) {
            // Có taskId - dùng serve-file servlet
            pdfServeUrl = request.getContextPath() + "/serve-file?taskId=" + guestTaskId + "&type=pdf";
        } else {
            // Chưa có taskId - dùng FileServeController với fileName
            String absolutePath = filePathObj.toString().replace("\\", "/");
            String fileNameOnly = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
            pdfServeUrl = request.getContextPath() + "/upload/" + fileNameOnly;
        }
    }

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

        <div id="statusContainer">
            <% if (taskStatus != null) { %>
                <div class="status-box">
                    <span class="status-label">Trạng thái:</span>
                    <span id="statusBadge" class="status-badge <%= isDone ? "done" : (isProcessing ? "processing" : "pending") %>">
                        <%= taskStatus %>
                    </span>
                    <p id="statusNote" class="status-note">
                        <% if (isProcessing) { %> Hệ thống đang xử lý file của bạn...
                        <% } else if (isDone) { %> Tệp đã sẵn sàng để tải xuống.
                        <% } else { %> Chờ khởi động...
                        <% } %>
                    </p>
                </div>
            <% } %>

            <div id="actionArea">
                <% if (canShowConvertButton) { %> 
                    <form action="<%= request.getContextPath() %>/convert" method="POST" class="convert-form" id="initialConvertForm">
                        <input type="hidden" name="filePath" value="<%= filePathObj %>">
                        <button type="submit" class="convert-btn">Convert sang Word</button>
                    </form>
                <% } %>
                
                <% if (isDone) { %>
                    <div id="downloadArea">
                        <form action="<%= request.getContextPath() %>/download" method="GET">
                            <input type="hidden" name="taskId" value="<%= guestTaskId %>">
                            <input type="hidden" name="type" value="docx">
                            <button type="submit" class="download-btn">Download DOCX</button>
                        </form>
                    </div>
                <% } else { %>
                    <div id="downloadArea"></div> 
                <% } %>
            </div>
            
            <% if (userId != null) { %>
                <div class="history-link">
                    <a href="<%= request.getContextPath() %>/history.jsp">Xem lịch sử</a>
                </div>
            <% } %>
        </div>

        <div class="action">
            <div class="text-center mt-3" style="margin-top: 20px;">
                <button type="button" class="btn-back" onclick="window.location.href='<%= request.getContextPath() %>/index.jsp'">
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
    // Biến cho PDF preview
    <% if (pdfServeUrl != null) { %>
        const pdfPath = "<%= pdfServeUrl %>";
    <% } else { %>
        const pdfPath = "";
    <% } %>
    
    // Biến cho status polling
    <% if (guestTaskId != null) { %>
        const TASK_ID = <%= guestTaskId %>;
    <% } else { %>
        const TASK_ID = null;
    <% } %>
    
    <% if (taskStatus != null) { %>
        const INITIAL_STATUS = "<%= taskStatus %>";
    <% } else { %>
        const INITIAL_STATUS = "UNKNOWN";
    <% } %>
    
    const CONTEXT_PATH = "<%= request.getContextPath() %>";
</script>

<script src="<%= request.getContextPath() %>/js/pdf-preview.js"></script>
<script src="<%= request.getContextPath() %>/js/convert-page.js"></script>

