<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - PDF Converter</title>
    <link rel="stylesheet" href="styles1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />

    <link rel="icon" href="logo.png" type="image/png">
</head>
<% if (request.getParameter("error") != null) { %>
<div class="alert alert-danger text-center" role="alert">
    Invalid email or password!
</div>
<% } %>

<div class="login-container d-flex justify-content-center align-items-center vh-100">
    <div class="login-card bg-white rounded-4 shadow p-5" style="max-width: 500px; width: 100%;">
        <div class="text-center mb-4">
            <div class="logo mb-3">
                <svg width="48" height="48" viewBox="0 0 32 32" fill="none">
                    <rect width="32" height="32" rx="6" fill="#0d6efd"/>
                    <path d="M8 12h16v2H8v-2zm0 4h16v2H8v-2zm0 4h10v2H8v-2z" fill="white"/>
                </svg>
            </div>
            <h3 class="fw-bold">Đăng nhập vào PDF Converter</h3>
            <p class="text-muted mb-0">Chào mừng bạn trở lại! Hãy đăng nhập để tiếp tục</p>
        </div>

        <form action="login" method="post" novalidate>
            <div class="form-floating mb-3">
                <input type="email" class="form-control" id="email" name="email" placeholder="email@example.com" required>
                <label for="email">Email</label>
            </div>

            <div class="form-floating mb-3 position-relative">
                <input type="password" class="form-control" id="password" name="password" placeholder="Password" required>
                <label for="password">Password</label>
                <button type="button" class="btn btn-sm position-absolute end-0 top-50 translate-middle-y me-3 border-0 bg-transparent"
                        onclick="togglePassword()">
                    <i class="fa-solid fa-eye" id="toggleIcon"></i>
                </button>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="remember" checked>
                    <label class="form-check-label" for="remember">Ghi nhớ đăng nhập</label>
                </div>
                <a href="#" class="text-decoration-none small">Quên mật khẩu</a>
            </div>

            <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">
                Đăng nhập
            </button>

            <div class="text-center my-3 text-muted">Hoặc tiếp tục với</div>

            <div class="text-center mt-3">
                <a href="#" class="btn btn-outline-danger w-50 me-2">
                    <i class="fab fa-google me-2"></i> Google
                </a>
            </div>

            <p class="text-center mt-4">
                Bạn chưa có tài khoản?
                <a href="register.jsp" class="text-decoration-none fw-semibold">Đăng ký ngay</a>
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

<script>
    function togglePassword() {
        const pass = document.getElementById("password");
        const icon = document.getElementById("toggleIcon");
        if (pass.type === "password") {
            pass.type = "text";
            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");
        } else {
            pass.type = "password";
            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");
        }
    }
</script>

</body>
</html>
