const TASK_ID_GLOBAL = typeof TASK_ID !== 'undefined' ? TASK_ID : null;
const INITIAL_STATUS_GLOBAL = typeof INITIAL_STATUS !== 'undefined' ? INITIAL_STATUS : null;
const CONTEXT_PATH_GLOBAL = typeof CONTEXT_PATH !== 'undefined' ? CONTEXT_PATH : '';

const STATUS_INTERVAL_MS = 4000; 
let pollingTimer;


/**
 * 1. Cập nhật DOM với trạng thái mới từ API (KHÔNG reload trang).
 * @param {string} status - Trạng thái mới (DONE, PROCESSING, FAILED).
 * @param {string} docxPath - Đường dẫn DOCX (có giá trị nếu DONE).
 */
function updateDomStatus(status, docxPath) {
    const statusBadge = document.getElementById('statusBadge');
    const statusNote = document.getElementById('statusNote');
    const downloadArea = document.getElementById('downloadArea');
    const initialConvertForm = document.getElementById('initialConvertForm'); 
    const statusContainer = document.getElementById('statusContainer');
    
    if (status === 'DONE' || status === 'FAILED') {
        clearInterval(pollingTimer);
    }

    // Xác định class cho status badge
    let statusClass = 'pending';
    if (status === 'DONE') {
        statusClass = 'done';
    } else if (status === 'PROCESSING' || status === 'PENDING') {
        statusClass = 'processing';
    } else if (status === 'FAILED') {
        statusClass = 'failed';
    }
    
    // Cập nhật status badge
    if (statusBadge) {
        statusBadge.textContent = status;
        // Xóa tất cả các class cũ và thêm class mới
        statusBadge.className = 'status-badge ' + statusClass;
    }
    
    // Cập nhật status note
    if (statusNote) {
        if (status === 'DONE') {
            statusNote.textContent = 'Tệp đã sẵn sàng để tải xuống.';
        } else if (status === 'PROCESSING' || status === 'PENDING') {
            statusNote.textContent = 'Hệ thống đang xử lý file của bạn...';
        } else if (status === 'FAILED') {
            statusNote.textContent = 'Quá trình chuyển đổi thất bại. Vui lòng thử lại.';
        } else {
            statusNote.textContent = 'Chờ khởi động...';
        }
    }
    
    // Ẩn form convert khi đang xử lý hoặc đã xong
    if (initialConvertForm) {
        if (status === 'DONE' || status === 'PROCESSING' || status === 'PENDING') {
            initialConvertForm.style.display = 'none';
        } else {
            initialConvertForm.style.display = '';
        }
    }
    
    // Cập nhật download area
    if (status === 'DONE') {
        const downloadHTML = `
             <form action="${CONTEXT_PATH_GLOBAL}/download" method="GET" style="display:inline-block;">
                <input type="hidden" name="taskId" value="${TASK_ID_GLOBAL}">
                <input type="hidden" name="type" value="docx">
                <button type="submit" class="download-btn"><i class="fas fa-download"></i> Download DOCX</button>
            </form>
            <form action="${CONTEXT_PATH_GLOBAL}/download" method="GET" style="display:inline-block; margin-right: 5px;">
                <input type="hidden" name="taskId" value="${TASK_ID_GLOBAL}">
                <input type="hidden" name="type" value="pdf">
                <button type="submit" class="download-btn"><i class="fas fa-download"></i> Download PDF (Original)</button>
            </form>
        `;
        if (downloadArea) downloadArea.innerHTML = downloadHTML;
    } else if (status === 'PROCESSING' || status === 'PENDING') {
        if (downloadArea) downloadArea.innerHTML = '';
    }
    
    // Đảm bảo status container được hiển thị
    if (statusContainer && statusContainer.style.display === 'none') {
        statusContainer.style.display = '';
    }
}

function pollTaskStatus() {
    fetch(CONTEXT_PATH_GLOBAL + '/api/status?taskId=' + TASK_ID_GLOBAL, { cache: 'no-store' })
        .then(res => {
            if (res.status === 200) return res.json();
            throw new Error('API request failed');
        })
        .then(data => {
            if (data && data.status) {
                updateDomStatus(data.status, data.docxPath);
            }
        })
        .catch(err => {
            console.error("Error during AJAX polling:", err);
            clearInterval(pollingTimer); 
        });
}

window.addEventListener("DOMContentLoaded", function () {
    // Render PDF thumbnail nếu có pdfPath
    if (typeof pdfPath !== 'undefined' && pdfPath !== null && pdfPath !== '' && pdfPath !== 'null') {
        console.log("Rendering PDF thumbnail from:", pdfPath);
        if (typeof renderPdfThumbnail === 'function') {
            renderPdfThumbnail(pdfPath);
        } else {
            console.warn("renderPdfThumbnail function not found");
        }
    } else {
        console.log("No PDF path available for thumbnail");
    }
    
    // Bắt đầu polling status nếu có taskId và status đang xử lý
    if (TASK_ID_GLOBAL && (INITIAL_STATUS_GLOBAL === 'PROCESSING' || INITIAL_STATUS_GLOBAL === 'PENDING' || INITIAL_STATUS_GLOBAL === 'UPLOADED')) {
        console.log("Polling started for Task ID:", TASK_ID_GLOBAL, "with initial status:", INITIAL_STATUS_GLOBAL);
        pollingTimer = setInterval(pollTaskStatus, STATUS_INTERVAL_MS);
    } else {
        console.log("Status polling not started. Task ID:", TASK_ID_GLOBAL, "Initial Status:", INITIAL_STATUS_GLOBAL);
    }
});