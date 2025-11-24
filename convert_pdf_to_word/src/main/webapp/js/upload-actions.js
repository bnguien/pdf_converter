// Ẩn alert sau 3 giây
setTimeout(() => {
    document.querySelectorAll(".alert-success").forEach(a => {
        a.style.animation = "fadeOut .3s forwards";
        setTimeout(() => a.remove(), 400);
    });
}, 3000);

// Auto submit khi chọn file
document.addEventListener("DOMContentLoaded", function () {
    const fileInput = document.getElementById("fileInput");
    const uploadForm = document.getElementById("uploadForm");

    if (fileInput && uploadForm) {
        fileInput.addEventListener("change", function () {
            if (this.value) uploadForm.submit();
        });
    }
});
