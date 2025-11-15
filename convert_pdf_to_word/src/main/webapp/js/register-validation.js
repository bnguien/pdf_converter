// Regex mật khẩu mạnh
const PASSWORD_REGEX =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

// Validate khi submit
function validatePasswords() {
    const pass = document.getElementById("password").value;
    const confirm = document.getElementById("confirmPassword").value;
    const passField = document.getElementById("password");
    const confirmField = document.getElementById("confirmPassword");

    const passFeedback = document.getElementById("password-feedback");
    const confirmFeedback = document.getElementById("confirm-password-feedback");

    // Reset
    passFeedback.textContent = "";
    confirmFeedback.textContent = "";
    passField.classList.remove("is-invalid", "is-valid");
    confirmField.classList.remove("is-invalid", "is-valid");

    let isValid = true;

    // Check password
    if (!pass.trim()) {
        passFeedback.textContent = "Vui lòng nhập mật khẩu!";
        passField.classList.add("is-invalid");
        isValid = false;
    } else if (!PASSWORD_REGEX.test(pass)) {
        passFeedback.textContent =
            "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký hiệu đặc biệt!";
        passField.classList.add("is-invalid");
        isValid = false;
    } else {
        passField.classList.add("is-valid");
    }

    // Confirm password
    if (!confirm.trim()) {
        confirmFeedback.textContent = "Vui lòng xác nhận mật khẩu!";
        confirmField.classList.add("is-invalid");
        isValid = false;
    } else if (pass !== confirm) {
        confirmFeedback.textContent = "Mật khẩu xác nhận không khớp!";
        confirmField.classList.add("is-invalid");
        isValid = false;
    } else {
        confirmField.classList.add("is-valid");
    }

    if (!isValid) {
        const firstInvalid = document.querySelector(".is-invalid");
        if (firstInvalid) {
            firstInvalid.scrollIntoView({ behavior: "smooth", block: "center" });
            firstInvalid.focus();
        }
        return false;
    }

    showLoading();
    return true;
}

// Real-time validation
document.addEventListener("DOMContentLoaded", function () {
    const passField = document.getElementById("password");
    const confirmField = document.getElementById("confirmPassword");
    const passFeedback = document.getElementById("password-feedback");
    const confirmFeedback = document.getElementById("confirm-password-feedback");

    function validatePassLive() {
        const pass = passField.value;

        if (!pass) {
            passField.classList.remove("is-invalid", "is-valid");
            passFeedback.textContent = "";
            return;
        }

        if (!PASSWORD_REGEX.test(pass)) {
            passField.classList.add("is-invalid");
            passField.classList.remove("is-valid");
            passFeedback.textContent =
                "Mật khẩu phải có ít nhất 8 ký tự gồm chữ hoa, chữ thường, số và ký hiệu!";
        } else {
            passField.classList.add("is-valid");
            passField.classList.remove("is-invalid");
            passFeedback.textContent = "";
        }
    }

    function validateConfirmPassLive() {
        const pass = passField.value;
        const confirm = confirmField.value;

        if (!confirm) {
            confirmField.classList.remove("is-invalid", "is-valid");
            confirmFeedback.textContent = "";
            return;
        }

        if (pass !== confirm) {
            confirmField.classList.add("is-invalid");
            confirmField.classList.remove("is-valid");
            confirmFeedback.textContent = "Mật khẩu xác nhận không khớp!";
        } else {
            confirmField.classList.add("is-valid");
            confirmField.classList.remove("is-invalid");
            confirmFeedback.textContent = "";
        }
    }

    passField.addEventListener("input", validatePassLive);
    passField.addEventListener("blur", validatePassLive);

    confirmField.addEventListener("input", validateConfirmPassLive);
    confirmField.addEventListener("blur", validateConfirmPassLive);
});

// Toggle password
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

// Loading overlay
function showLoading() {
    document.getElementById("loadingOverlay").style.display = "flex";

    const btn = document.getElementById("submitBtn");
    document.getElementById("submitText").textContent = "Đang xử lý...";
    document.getElementById("submitSpinner").classList.remove("d-none");

    btn.disabled = true;
}
