(function () {
    // Lấy tham số từ JSP
    var status = window.STATUS_FROM_JSP || "";
    var msgCode = window.MSG_CODE_FROM_JSP || "";

    function getFriendlyMessage(code) {
        switch (code) {
            case 'invalid_file_type': return 'Loại tệp không hợp lệ. Vui lòng chọn tệp PDF.';
            case 'db_save_failed': return 'Lỗi lưu vào cơ sở dữ liệu. Vui lòng thử lại.';
            case 'system_io_error': return 'Lỗi hệ thống. Vui lòng thử lại sau.';
            default: return 'Đã xảy ra lỗi trong quá trình tải tệp.';
        }
    }

    if (status === 'error') {
        let overlay = document.getElementById('errorOverlay');
        let overlayMsg = document.getElementById('overlayMessage');

        overlayMsg.textContent = getFriendlyMessage(msgCode);
        overlay.style.display = 'flex';

        document.getElementById('closeOverlay').onclick = () => {
            overlay.style.display = 'none';

            // Xóa tham số URL
            let url = new URL(window.location.href);
            url.searchParams.delete("status");
            url.searchParams.delete("msg");
            window.history.replaceState({}, document.title, url.pathname + url.search);
        };
    }
})();
