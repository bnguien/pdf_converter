// Auto-hide alert Bootstrap sau 3s
document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
        const alerts = document.querySelectorAll(".alert-toast");
        alerts.forEach(function (alert) {
            const bsAlert =
                bootstrap.Alert.getInstance(alert) || new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 3000);
});
