// Tự đóng alert sau 3 giây
document.addEventListener("DOMContentLoaded", function () {
    setTimeout(() => {
        const alerts = document.querySelectorAll(".alert-toast");
        alerts.forEach(alert => {
            const bsAlert =
                bootstrap.Alert.getInstance(alert) || new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 3000);
});
