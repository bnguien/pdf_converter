// Hiện/ẩn mật khẩu
function togglePassword() {
    const pass = document.getElementById("password");
    const icon = document.getElementById("toggleIcon");

    if (pass.type === "password") {
        pass.type = "text";
        icon.classList.replace("fa-eye", "fa-eye-slash");
    } else {
        pass.type = "password";
        icon.classList.replace("fa-eye-slash", "fa-eye");
    }
}

// Hiển thị loading khi submit
function showLoading() {
    document.getElementById("loadingOverlay").style.display = "flex";

    const submitBtn = document.getElementById("submitBtn");
    const submitText = document.getElementById("submitText");
    const submitSpinner = document.getElementById("submitSpinner");

    submitBtn.disabled = true;
    submitText.textContent = "Đang xử lý...";
    submitSpinner.classList.remove("d-none");
}
