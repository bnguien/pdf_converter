// Regex mật khẩu
const PASSWORD_REGEX =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

// Validate khi submit form
function validatePasswords() {
    const pass = document.getElementById("password").value;
    const confirm = document.getElementById("confirmPassword").value;
    const passField = document.getElementById("password");
    const confirmField = document.getElementById("confirmPassword");
    const passFeedback = document.getElementById("password-feedback");
    const confirmFeedback = document.getElementById("confirm-password-feedback");

    let isValid = true;

    // Clear trạng thái
    passFeedback.textContent = "";
    confirmFeedback.textContent = "";
    passField.classList.remove("is-invalid", "is-valid");
    confirmField.classList.remove("is-invalid", "is-valid");

    // Kiểm tra mật khẩu rỗng
    if (!pass.trim()) {
        passFeedback.textContent = "Vui lòng nhập mật khẩu!";
        passField.classList.add("is-invalid");
        isValid = false;
    }
    // Kiểm tra regex
    else if (!PASSWORD_REGEX.test(pass)) {
        passFeedback.textContent =
            "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký hiệu đặc biệt (@$!%*?&)!";
        passField.classList.add("is-invalid");
        isValid = false;
    } else {
        passField.classList.add("is-valid");
    }

    // Kiểm tra mật khẩu xác nhận
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
        if (firstInvalid) firstInvalid.scrollIntoView({ behavior: "smooth", block: "center" });
        return false;
    }

    showLoading();
    return true;
}

// Real-time validation
function setupRealTimeValidation() {
    const passField = document.getElementById("password");
    const confirmField = document.getElementById("confirmPassword");
    const passFeedback = document.getElementById("password-feedback");
    const confirmFeedback = document.getElementById("confirm-password-feedback");

    passField.addEventListener("input", function () {
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
                "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký hiệu đặc biệt!";
        } else {
            passField.classList.add("is-valid");
            passField.classList.remove("is-invalid");
            passFeedback.textContent = "";
        }
    });

    confirmField.addEventListener("input", function () {
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
    });
}

// Hiện / Ẩn mật khẩu
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
