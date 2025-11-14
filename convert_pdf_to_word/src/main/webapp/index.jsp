<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>

<%
    Integer userId = (Integer) session.getAttribute("user_id");
    String email = (String) session.getAttribute("user_email");
    String username = (String) session.getAttribute("user_username");

    String statusParamTop = request.getParameter("status");
    String uploadParamTop = request.getParameter("upload");
    String pdfNameParamTop = request.getParameter("pdfName");
    String taskIdParamTop = request.getParameter("taskId");
    String msgParamTop = request.getParameter("msg");
%>

<%@ include file="/header.jsp" %>

<div class="main">
    <div class="converter">
        <div class="converter_container">

            <div class="converter_header">
                <div class="converter_icon"><i class="fa-solid fa-file-pdf"></i></div>
                <div class="converter_text">
                    <h2 class="converter_title">Công cụ chuyển đổi PDF trực tuyến</h2>
                    <p class="converter_subtitle">Dễ dàng chuyển PDF sang Word chỉ trong vài giây.</p>
                </div>
            </div>

            <% if ("success".equals(uploadParamTop)) { %>

                <div class="uploaded-area">
                    <div class="uploaded-icon"><i class="fa-solid fa-file-pdf"></i></div>
                    <div class="uploaded-title">Tệp đã tải lên</div>
                    <div class="uploaded-filename"><%= pdfNameParamTop %></div>

                    <form action="<%= request.getContextPath() %>/convert" method="post" class="convert-form">
                        <input type="hidden" name="taskId" value="<%= taskIdParamTop %>">
                        <button type="submit" class="converter_btn">Convert to Word</button>
                    </form>
                </div>

            <% } else { %>

                <form action="<%= request.getContextPath() %>/upload" method="post" enctype="multipart/form-data" id="uploadForm">
                    <input type="file" name="file" id="fileInput" style="display:none" accept="application/pdf,.pdf" required>
                    <div class="converter_upload">
                        <button type="button" class="converter_btn" onclick="document.getElementById('fileInput').click()">
                            <i class="fa-solid fa-upload"></i><span>Chọn tệp từ máy</span>
                        </button>
                    </div>
                </form>

            <% } %>

        </div>
    </div>

</div>

<div id="errorOverlay" style="display:none;">
    <div class="overlay-box">
        <div class="overlay-header">
            <h4 class="overlay-title">Lỗi khi tải tệp</h4>
            <button id="closeOverlay" class="overlay-close">&times;</button>
        </div>
        <div id="overlayMessage"></div>
    </div>
</div>

<script>
    setTimeout(() => {
        document.querySelectorAll(".alert-success").forEach(a => {
            a.style.animation = "fadeOut .3s forwards";
            setTimeout(() => a.remove(), 300);
        });
    }, 3000);

    var _fileInput = document.getElementById("fileInput");
    if (_fileInput) {
        _fileInput.addEventListener("change", function () {
            if (this.value) document.getElementById("uploadForm").submit();
        });
    }

    function handleDriveUpload() {}

    (function () {
        var status = '<%= statusParamTop != null ? statusParamTop : "" %>';
        var msgCode = '<%= msgParamTop != null ? msgParamTop : "" %>';

        function getFriendlyMessage(code) {
            switch (code) {
                case 'invalid_file_type': return 'Loại tệp không hợp lệ. Vui lòng chọn tệp PDF.';
                case 'db_save_failed': return 'Lỗi lưu vào cơ sở dữ liệu. Vui lòng thử lại.';
                case 'system_io_error': return 'Lỗi hệ thống. Vui lòng thử lại sau.';
                default: return 'Đã xảy ra lỗi trong quá trình tải tệp.';
            }
        }

        if (status === 'error') {
            let overlay = document.getElementById('errorOverlay');
            let overlayMsg = document.getElementById('overlayMessage');
            overlayMsg.textContent = getFriendlyMessage(msgCode);
            overlay.style.display = 'flex';

            document.getElementById('closeOverlay').onclick = () => {
                overlay.style.display = 'none';
                let url = new URL(window.location.href);
                url.searchParams.delete("status");
                url.searchParams.delete("msg");
                window.history.replaceState({}, document.title, url.pathname + url.search);
            };
        }
    })();
</script>

<%@ include file="/footer.jsp" %>
