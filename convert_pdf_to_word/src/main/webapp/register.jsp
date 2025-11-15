<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <title>Register - PDF Converter</title>
        <link rel="stylesheet" href="css/styles1.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <link rel="icon" href="logo.png" type="image/png">
        <link rel="stylesheet" href="css/styles1.css">
    </head>
    <div class="loading-overlay" id="loadingOverlay">
        <div class="loading-spinner"></div>
    </div>
    <% if (request.getParameter("error") !=null) { %>
        <div class="alert alert-danger alert-toast alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i>
            Email đã tồn tại! Vui lòng thử email khác.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>
            <% if (request.getParameter("mismatch") !=null) { %>
                <div class="alert alert-warning alert-toast alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Mật khẩu không khớp! Vui lòng kiểm tra lại.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                    <% if (request.getParameter("format_error") !=null) { %>
                        <div class="alert alert-danger alert-toast alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <% if ("email".equals(request.getParameter("format_error"))) { %>
                                Email không hợp lệ!
                                <% } else if ("password".equals(request.getParameter("format_error"))) { %>
                                    Mật khẩu yếu! Mật khẩu phải có tối thiểu 8 ký tự, bao gồm chữ hoa, chữ thường, số và
                                    ký hiệu đặc biệt.
                                    <% } else { %>
                                        Vui lòng điền đầy đủ thông tin!
                                        <% } %>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                            <div class="register-container">
                                <div class="register-card bg-white rounded-4 shadow p-5"
                                    style="max-width: 500px; width: 100%;">
                                    <div class="text-center mb-4">
                                        <div class="logo mb-3">
                                            <svg width="48" height="48" viewBox="0 0 32 32" fill="none">
                                                <rect width="32" height="32" rx="6" fill="#0d6efd" />
                                                <path d="M8 12h16v2H8v-2zm0 4h16v2H8v-2zm0 4h10v2H8v-2z" fill="white" />
                                            </svg>
                                        </div>
                                        <h3 class="fw-bold">Tạo tài khoản PDF Converter cho riêng bạn</h3>
                                        <p class="text-muted mb-0">Tham gia ngay với chúng tôi và bắt đầu chuyển đổi tài
                                            liệu của bạn một cách miễn phí</p>
                                    </div>

                                    <form action="register" method="post" onsubmit="return validatePasswords();">
                                        <div class="form-floating mb-3">
                                            <input type="text" class="form-control" id="username" name="username"
                                                placeholder="JohnDoe" required>
                                            <label for="username">Tên người dùng</label>
                                        </div>

                                        <div class="form-floating mb-3">
                                            <input type="email" class="form-control" id="email" name="email"
                                                placeholder="email@example.com" required>
                                            <label for="email">Email</label>
                                        </div>

                                        <div class="form-floating mb-3 position-relative">
                                            <input type="password" class="form-control" id="password" name="password"
                                                placeholder="Password" required
                                                pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$">
                                            <label for="password">Mật khẩu</label>
                                            <button type="button" class="password-toggle" onclick="togglePassword()">
                                                <i class="fa-solid fa-eye" id="toggleIcon"></i>
                                            </button>
                                            <div id="password-feedback" class="invalid-feedback"
                                                style="display: block; font-size: 0.875rem;"></div>
                                        </div>

                                        <div class="form-floating mb-3">
                                            <input type="password" class="form-control" id="confirmPassword"
                                                name="confirmPassword" placeholder="Confirm Password" required>
                                            <label for="confirmPassword">Xác nhận mật khẩu</label>
                                            <div id="confirm-password-feedback" class="invalid-feedback"
                                                style="display: block; font-size: 0.875rem;"></div>
                                        </div>

                                        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold"
                                            id="submitBtn">
                                            <span id="submitText">Đăng ký</span>
                                            <span id="submitSpinner" class="spinner-border spinner-border-sm d-none"
                                                role="status"></span>
                                        </button>
                                        <div class="text-center my-3 text-muted">Hoặc tiếp tục với</div>

                                        <div class="text-center mt-3">
                                            <a href="#" class="btn btn-outline-danger w-50 me-2">
                                                <i class="fab fa-google me-2"></i> Google
                                            </a>
                                        </div>

                                        <p class="text-center mt-4">
                                            Bạn đã có tài khoản?
                                            <a href="login" class="text-decoration-none fw-semibold text-primary">Đăng
                                                nhập ngay</a>
                                        </p>
                                    </form>
                                    <div class="text-center mt-3">
                                        <button type="button" class="btn btn-primary w-100 py-2 fw-semibold"
                                            onclick="window.location.href='index.jsp'">
                                            Quay lại
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <script
                                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

                            <script src="<%= request.getContextPath() %>/js/register-validation.js"></script>
                            <script src="<%= request.getContextPath() %>/js/register-alert.js"></script>
                            </body>

    </html>