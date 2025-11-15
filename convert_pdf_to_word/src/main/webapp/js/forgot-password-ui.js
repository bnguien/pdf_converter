// Loading spinner
function showLoading() {
    document.getElementById("loadingOverlay").style.display = "flex";

    const btn = document.getElementById("submitBtn");
    document.getElementById("submitText").textContent = "Đang xử lý...";
    document.getElementById("submitSpinner").classList.remove("d-none");

    btn.disabled = true;
}

// Auto hide alert + setup realtime validation
document.addEventListener("DOMContentLoaded", function () {
    setupRealTimeValidation();

    setTimeout(function () {
        const alerts = document.querySelectorAll(".alert-toast");
        alerts.forEach(function (alert) {
            if (typeof bootstrap !== "undefined") {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        });
    }, 3000);
});
