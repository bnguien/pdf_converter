<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%
    String email = (String) session.getAttribute("user_email");
    String username = (String) session.getAttribute("user_username");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>PDF Converter</title>
    <link rel="stylesheet" href="css/styles.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>

<body>
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
            <li><span class="welcome">Xin chào, <%= username %></span></li>
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
    const mobileNav = document.querySelector(".hamburger");
    const navbar = document.querySelector(".menubar");

    const toggleNav = () => {
        navbar.classList.toggle("active");
        mobileNav.classList.toggle("hamburger-active");
    };
    mobileNav.addEventListener("click", () => toggleNav());
</script>
</body>
</html>
