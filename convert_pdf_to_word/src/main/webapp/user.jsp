<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page session="true" %>

        <% String username=(String) session.getAttribute("user_username"); String email=(String)
            session.getAttribute("user_email"); String role=(String) session.getAttribute("user_role"); Integer
            userId=(Integer) session.getAttribute("user_id"); %>
            <link rel="stylesheet" href="<%= request.getContextPath() %>/css/user.css">
            <div class="user-card">
                <div class="user-avatar">
                    <%= username !=null && !username.isEmpty() ? username.toUpperCase().charAt(0) : "U" %>
                </div>
                <div class="user-info">
                    <h3>Thông tin tài khoản</h3>
                    <div class="user-info-item">
                        <span class="user-info-label">
                            <i class="fas fa-user"></i>
                            Tên người dùng
                        </span>
                        <span class="user-info-value">
                            <%= username !=null ? username : "N/A" %>
                        </span>
                    </div>
                    <div class="user-info-item">
                        <span class="user-info-label">
                            <i class="fas fa-envelope"></i>
                            Email
                        </span>
                        <span class="user-info-value">
                            <%= email !=null ? email : "N/A" %>
                        </span>
                    </div>

                    <% if (role !=null) { %>
                        <div class="user-info-item">
                            <span class="user-info-label">
                                <i class="fas fa-shield-alt"></i>
                                Vai trò
                            </span>
                            <span class="user-info-value">
                                <span class="role-badge">
                                    <%= role %>
                                </span>
                            </span>
                        </div>
                        <% } %>

                            <% if (userId !=null) { %>
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
            <div class="action">
                <div class="text-center mt-3" style="margin-top: 20px;">
                    <button type="button" class="btn-back" onclick="window.location.href='index.jsp'">
                        Quay lại
                    </button>
                </div>
            </div>