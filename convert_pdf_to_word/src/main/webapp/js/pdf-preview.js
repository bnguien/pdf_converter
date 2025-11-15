function renderPdfThumbnail(pdfUrl) {
    if (!pdfUrl || pdfUrl === "null") {
        console.error("Không tìm thấy đường dẫn PDF để preview.");
        return;
    }

    pdfjsLib.getDocument(pdfUrl).promise
        .then(function (pdf) {
            return pdf.getPage(1);
        })
        .then(function (page) {
            const viewport = page.getViewport({ scale: 0.5 });
            const canvas = document.getElementById("pdfThumb");
            const context = canvas.getContext("2d");

            canvas.width = viewport.width;
            canvas.height = viewport.height;

            page.render({
                canvasContext: context,
                viewport: viewport
            });
        })
        .catch(function (error) {
            console.error("Lỗi load PDF:", error);
        });
}
