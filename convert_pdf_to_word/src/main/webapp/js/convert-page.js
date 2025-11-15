window.addEventListener("DOMContentLoaded", function () {
    if (typeof pdfPath !== "undefined" && pdfPath) {
        renderPdfThumbnail(pdfPath);
    } else {
        console.warn("pdfPath không tồn tại hoặc null.");
    }
});
