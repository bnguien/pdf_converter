<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>PDF Converter</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
</head>

<body>

<div class="header">
    <nav class="navbar">
        <div class="logo"><h1>PDF Converter</h1></div>

        <ul class="menu">

            <% if (session.getAttribute("user_email") != null) { %>

                <li><a href="index" class="active">Trang chủ</a></li>
                <li><a href="history">Lịch sử</a></li>

                <li>
                    <div class="welcome-container">

                        <div class="welcome-avatar">
                            <%
                                Object u = session.getAttribute("user_username");
                                if (u != null && !u.toString().isEmpty()) {
                            %>
                                <%= Character.toUpperCase(u.toString().charAt(0)) %>
                            <% } else { %>
                                U
                            <% } %>
                        </div>

                        <a href="user" class="welcome-link">
                            Xin chào, <%= session.getAttribute("user_username") %>
                        </a>
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
