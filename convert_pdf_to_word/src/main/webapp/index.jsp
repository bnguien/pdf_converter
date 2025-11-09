<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%
    Integer userId = (Integer) session.getAttribute("user_id");
    String email = (String) session.getAttribute("user_email");
    String username = (String) session.getAttribute("user_username");
    String role = (String) session.getAttribute("user_role");

    // Parameters returned from UploadController
    String statusParamTop = request.getParameter("status");
    String uploadParamTop = request.getParameter("upload");
    String pdfNameParamTop = request.getParameter("pdfName");
    String taskIdParamTop = request.getParameter("taskId");
    String msgParamTop = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>PDF Converter</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="css/styles.css"/>
    <!--Google API-->
    <script type="text/javascript" src="https://apis.google.com/js/api.js"></script>
    <script type="text/javascript" src="https://apis.google.com/js/client.js"></script>
</head>

<body>
<% if (request.getParameter("success") != null && "login".equals(request.getParameter("success"))) { %>
<div class="alert alert-success alert-toast alert-dismissible fade show" role="alert">
    <i class="fas fa-check-circle me-2"></i>
    Đăng nhập thành công! Chào mừng trở lại, <%= username %>!
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<% } %>
<div class="header">
    <nav class="navbar">
        <div class="logo">
            <h1>PDF Converter</h1>
        </div>
        <ul class="menu">
            <% if (email != null) { %>
            <li><a href="index" class="active">Trang chủ</a></li> 
            <li><a href="convert">Chuyển đổi</a></li>
            <li><a href="history">Lịch sử</a></li>
            <li>
                <div class="welcome-container">
                    <div class="welcome-avatar"><%= username != null && !username.isEmpty() ? Character.toUpperCase(username.charAt(0)) : "U" %></div>
                    <span class="welcome">Xin chào, <%= username %></span>
                </div>
            </li>
            <li><a href="logout" class="btn btn-logout">Đăng xuất</a></li>
            <% } else { %>
            <li><a href="login" class="btn btn-login">Đăng nhập</a></li>
            <li><a href="register" class="btn btn-register">Đăng ký</a></li>
            <% } %>
        </ul>
    </nav>
</div>
<div class="main">
    <% if (email == null) { %>
    <div class="hero">
        <h1 class="hero_header1">Make your work</h1>
        <h1 class="hero_header2">easier.</h1>
        <p class="hero_description">Chuyển PDF sang Word dễ dàng, nhanh và chính xác chỉ trong một cú nhấp</p>
        <a href="login" class="btn btn-login">
            Bắt đầu ngay
        </a>
    </div>
    <% } else { %>
    <%@ include file="user.jsp" %>
    <div class="converter">
        <div class="converter_container">
            <div class="converter_header">
                <div class="converter_icon">
                    <i class="fa-solid fa-file-pdf"></i>
                </div>
                <div class="converter_text">
                    <h2 class="converter_title">Công cụ chuyển đổi PDF trực tuyến</h2>
                    <p class="converter_subtitle">Dễ dàng chuyển PDF sang Word chỉ trong vài giây.</p>
                </div>
            </div>

            <% 
               String uploadParam = request.getParameter("upload");
               String statusParam = request.getParameter("status");
               String pdfNameParam = request.getParameter("pdfName");
               String taskIdParam = request.getParameter("taskId");
               String msgParam = request.getParameter("msg");
            %>

            <% if ("success".equals(uploadParam)) { %>
                <div class="uploaded-area">
                    <div class="uploaded-icon"><i class="fa-solid fa-file-pdf"></i></div>
                    <div class="uploaded-title">Tệp đã tải lên</div>
                    <div class="uploaded-filename"> 
                        <%= (pdfNameParam != null) ? pdfNameParam : "(tên tệp không xác định)" %> 
                    </div>
                    <form action="<%= request.getContextPath() %>/convert" method="post" class="convert-form">
                        <input type="hidden" name="taskId" value="<%= (taskIdParam != null) ? taskIdParam : "" %>">
                        <button type="submit" class="converter_btn">Convert to Word</button>
                    </form>
                </div>
            <% } else { %>
                <form action="<%= request.getContextPath() %>/upload" method="post" enctype="multipart/form-data" id="uploadForm">
                    <input type="file" name="file" id="fileInput" style="display: none" accept="application/pdf,.pdf" required>
                    <div class="converter_upload">
                        <button type="button" class="converter_btn" onclick="document.getElementById('fileInput').click()">
                            <i class="fa-solid fa-upload"></i>
                            <span>Chọn tệp từ máy</span>
                        </button>
                    </div>
                </form>

                <p class="converter_subtitle">Hoặc</p>

                <form action="uploadDrive" method="post" id="driveUploadForm">
                    <input type="hidden" name="driveFileId" id="driveFileId">
                    <input type="hidden" name="driveFileName" id="driveFileName">
                    <div class="converter_upload">
                        <button type="button" class="converter_btn" onclick="handleDriveUpload()">
                            <i class="fa-brands fa-google-drive"></i>
                            <span>Google Drive</span>
                        </button>
                    </div>
                </form>
            <% } %>
        </div>
    </div>
    <% } %>
</div>
<div class="footer">
    <p>© 2025 PDF Converter. All rights reserved.</p>
</div>

<!-- Overlay for error messages -->
<div id="errorOverlay" style="display: none;">
    <div class="overlay-box">
        <div class="overlay-header">
            <h4 class="overlay-title">Lỗi khi tải tệp</h4>
            <button id="closeOverlay" class="overlay-close">&times;</button>
        </div>
        <div id="overlayMessage"></div>
    </div>
</div>

<script>
    // Auto-hide success message after 3 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll(".alert-toast");
        alerts.forEach(function(alert) {
            if (alert.classList.contains("alert-success")) {
                alert.style.animation = "fadeOut 0.3s ease-in forwards";
                setTimeout(function() {
                    alert.remove();
                }, 300);
            }
        });
    }, 3000);

    // Auto-submit when user upload file
    var _fileInput = document.getElementById('fileInput');
    if (_fileInput) {
        _fileInput.addEventListener('change', function() {
            if (this.value) {
                var frm = document.getElementById('uploadForm');
                if (frm) frm.submit();
            }
        });
    }

    // Google Drive Upload
    function handleDriveUpload() {
        // future integration
    }

    // Show error overlay if UploadController returned status=error
    (function() {
        var status = '<%= statusParamTop != null ? statusParamTop : "" %>';
        var msgCode = '<%= msgParamTop != null ? msgParamTop : "" %>';
        var pdfName = '<%= pdfNameParamTop != null ? pdfNameParamTop : "" %>';

        function getFriendlyMessage(code) {
            switch(code) {
                case 'invalid_file_type': return 'Loại tệp không hợp lệ. Vui lòng chọn tệp PDF.';
                case 'db_save_failed': return 'Lỗi lưu thông tin vào cơ sở dữ liệu. Vui lòng thử lại.';
                case 'system_io_error': return 'Lỗi hệ thống khi xử lý tệp. Vui lòng thử lại sau.';
                default: return 'Đã xảy ra lỗi trong quá trình tải tệp. Vui lòng thử lại.';
            }
        }

        if (status === 'error') {
            var overlay = document.getElementById('errorOverlay');
            var overlayMessage = document.getElementById('overlayMessage');
            overlayMessage.textContent = getFriendlyMessage(msgCode);
            overlay.style.display = 'flex';
            document.getElementById('closeOverlay').addEventListener('click', function() {
                overlay.style.display = 'none';
                if (window.history && window.history.replaceState) {
                    var url = new URL(window.location.href);
                    url.searchParams.delete('status');
                    url.searchParams.delete('msg');
                    window.history.replaceState({}, document.title, url.pathname + url.search);
                }
            });
        }
    })();
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
