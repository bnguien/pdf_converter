<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - PDF Converter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="icon" href="logo.png" type="image/png">
    <link rel="stylesheet" href="css/styles1.css">
</head>
<body>

    <% 
        String error = request.getParameter("error");
        String success = request.getParameter("success");
        String message = "";
        String alertClass = ""; 

        if (success != null) {
            // Trường hợp thành công (Redirect từ Controller)
            // (Thường không xảy ra vì Controller redirect sang login.jsp khi success)
        } else if (error != null) {
            switch (error) {
                case "invalid":
                    message = "Dữ liệu nhập vào hoặc định dạng Email không hợp lệ/Mật khẩu không khớp.";
                    alertClass = "alert-danger";
                    break;
                case "notExist":
                    message = "Thông tin tài khoản không khớp. Vui lòng đảm bảo Username và Email chính xác.";
                    alertClass = "alert-warning"; // Cảnh báo nghiệp vụ
                    break;
                case "exists": // Lỗi khi cập nhật DB (Controller dùng error=exists)
                    message = "Đã xảy ra lỗi hệ thống khi cập nhật mật khẩu. Vui lòng thử lại sau.";
                    alertClass = "alert-danger"; 
                    break;
                default:
                    message = "Đã xảy ra lỗi không xác định.";
                    alertClass = "alert-danger";
                    break;
            }
        }
    %>

    <% if (error != null) { %>
        <div class="alert <%= alertClass %> alert-toast alert-dismissible fade show" role="alert" 
             style="position: fixed; top: 10px; right: 10px; z-index: 1050;">
            <i class="fas fa-exclamation-triangle me-2"></i>
            <%= message %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <div class="loading-overlay" id="loadingOverlay">
        <div class="loading-spinner"></div>
    </div>

    <div class="forgot-password-container" style="display: flex; justify-content: center; align-items: center; min-height: 100vh;">
        <div class="forgot-password-card bg-white rounded-4 shadow p-5" style="max-width: 500px; width: 100%;">
            <div class="text-center mb-4">
                 <div class="logo mb-3">
                     <svg width="48" height="48" viewBox="0 0 32 32" fill="none">
                         <rect width="32" height="32" rx="6" fill="#0d6efd" />
                         <path d="M8 12h16v2H8v-2zm0 4h16v2H8v-2zm0 4h10v2H8v-2z" fill="white" />
                     </svg>
                 </div>
                 <h3 class="fw-bold">Đặt lại Mật khẩu</h3>
                 <p class="text-muted mb-0">Xác nhận tài khoản của bạn và tạo mật khẩu mới</p>
            </div>
        
            <form action="forgotPassword" method="post" onsubmit="return validatePasswords();">
                <div class="form-floating mb-3">
                    <input type="text" class="form-control" id="username" name="username" placeholder="u" required>
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
                    <label for="password">Mật khẩu mới</label>
                    <button type="button" class="password-toggle" onclick="togglePassword()">
                        <i class="fa-solid fa-eye" id="toggleIcon"></i>
                    </button>
                    <div id="password-feedback" class="invalid-feedback" style="display: block; font-size: 0.875rem;"></div>
                </div>
        
                <div class="form-floating mb-3">
                    <input type="password" class="form-control" id="confirmPassword"
                         name="confirmPassword" placeholder="Confirm Password" required>
                    <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                    <div id="confirm-password-feedback" class="invalid-feedback" style="display: block; font-size: 0.875rem;"></div>
                </div>
                
                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold" id="submitBtn">
                    <span id="submitText">Đặt lại Mật khẩu</span>
                    <span id="submitSpinner" class="spinner-border spinner-border-sm d-none" role="status"></span>
                </button>
            </form>
            
            <p class="text-center mt-4">
                <a href="login" class="text-decoration-none fw-semibold text-primary">Quay lại Đăng nhập</a>
            </p>
        </div>
    </div>
<script src="js/forgot-password-validation.js"></script>
<script src="js/forgot-password-ui.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>