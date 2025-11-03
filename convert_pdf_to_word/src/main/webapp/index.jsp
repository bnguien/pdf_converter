<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%
    Integer userId = (Integer) session.getAttribute("user_id");
    String email = (String) session.getAttribute("user_email");
    String username = (String) session.getAttribute("user_username");
    String role = (String) session.getAttribute("user_role");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>PDF Converter</title>
    <link rel="stylesheet" href="css/styles.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        .alert-toast {
            position: fixed;
            top: 20px;
            right: 20px;
            min-width: 300px;
            z-index: 9999;
            animation: slideIn 0.3s ease-out, fadeOut 0.3s ease-in 2.7s forwards;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        @keyframes slideIn {
            from {
                transform: translateX(400px);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        @keyframes fadeOut {
            to {
                opacity: 0;
                transform: translateX(400px);
            }
        }
        .user-card {
            background: white;
            border-radius: 16px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            animation: fadeInUp 0.5s ease-out;
        }
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        .user-avatar {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: linear-gradient(135deg, #c0322d 0%, #ff6b5b 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 2rem;
            font-weight: bold;
            margin: 0 auto 20px;
        }
        .user-info h3 {
            color: #222;
            margin-bottom: 10px;
            text-align: center;
        }
        .user-info-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
            gap: 10px;
        }
        .user-info-item:last-child {
            border-bottom: none;
        }
        .user-info-label {
            color: #666;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .user-info-value {
            color: #222;
            font-weight: 500;
        }
        .role-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 600;
            background: #c0322d;
            color: white;
        }
        .welcome-container {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 8px 16px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        .welcome-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: linear-gradient(135deg, #c0322d 0%, #ff6b5b 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 0.875rem;
            font-weight: bold;
        }
    </style>
</head>

<body>
<% if (request.getParameter("success") != null && "login".equals(request.getParameter("success"))) { %>
<div class="alert alert-success alert-toast alert-dismissible fade show" role="alert" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">
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
            <li><a href="index.jsp" class="active">Trang chủ</a></li>
            <li><a href="convert.jsp">Chuyển đổi</a></li>
            <li><a href="history.jsp">Lịch sử</a></li>
            <li>
                <div class="welcome-container">
                    <div class="welcome-avatar"><%= username != null && !username.isEmpty() ? Character.toUpperCase(username.charAt(0)) : "U" %></div>
                    <span class="welcome">Xin chào, <%= username %></span>
                </div>
            </li>
            <li><a href="logout" class="btn btn-logout">Đăng xuất</a></li>
            <% } else { %>
            <li><a href="login.jsp" class="btn btn-login">Đăng nhập</a></li>
            <li><a href="register.jsp" class="btn btn-register">Đăng ký</a></li>
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
        <a href="login.jsp" class="btn btn-login">
            Bắt đầu ngay
        </a>
    </div>
    <% } else { %>
    <div class="user-card" style="max-width: 600px; margin: 0 auto;">
        <div class="user-avatar">
            <%= username != null && !username.isEmpty() ? username.toUpperCase().charAt(0) : "U" %>
        </div>
        <div class="user-info">
            <h3>Thông tin tài khoản</h3>
            <div class="user-info-item">
                <span class="user-info-label">
                    <i class="fas fa-user"></i>
                    Tên người dùng
                </span>
                <span class="user-info-value"><%= username != null ? username : "N/A" %></span>
            </div>
            <div class="user-info-item">
                <span class="user-info-label">
                    <i class="fas fa-envelope"></i>
                    Email
                </span>
                <span class="user-info-value"><%= email != null ? email : "N/A" %></span>
            </div>
            <% if (role != null) { %>
            <div class="user-info-item">
                <span class="user-info-label">
                    <i class="fas fa-shield-alt"></i>
                    Vai trò
                </span>
                <span class="user-info-value">
                    <span class="role-badge"><%= role %></span>
                </span>
            </div>
            <% } %>
            <% if (userId != null) { %>
            <div class="user-info-item">
                <span class="user-info-label">
                    <i class="fas fa-id-badge"></i>
                    ID người dùng
                </span>
                <span class="user-info-value">#<%= userId %></span>
            </div>
            <% } %>
        </div>
    </div>
    <div class="converter">
        <div class="converter_container">
            <div class="converter_header">
                <div class="converter_icon">
                    <i class="fa-solid fa-file-pdf"></i>
                </div>
                <div class="converter_text">
                    <h2 class="converter_title">Công cụ chuyển đổi PDF trực tuyến</h2>
                    <p class="converter_subtitle">Dễ dàng chuyển đổi từ và thành PDF chỉ trong vài giây.</p>
                </div>
            </div>

            <div class="converter_upload">
                <button class="converter_btn">
                    <i class="fa-solid fa-upload"></i>
                    <span>Chọn tệp</span>
                </button>
            </div>

            <div class="converter_sources">
                <button class="converter_source"><i class="fa-brands fa-google-drive"></i></button>
                <button class="converter_source"><i class="fa-brands fa-dropbox"></i></button>
                <button class="converter_source"><i class="fa-solid fa-link"></i></button>
            </div>
        </div>
    </div>
    <% } %>
</div>
<div class="footer">
    <p>© 2025 PDF Converter. All rights reserved.</p>
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
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
