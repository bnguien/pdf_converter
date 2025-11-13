<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>

<%
    Object fileNameObj = request.getAttribute("fileName");
    Object filePathObj = request.getAttribute("filePath");

    String fileName = null;
    String fileUrl = null;

    if (fileNameObj != null) {
        fileName = fileNameObj.toString();
    }

    if (filePathObj != null) {
        String absolutePath = filePathObj.toString().replace("\\", "/");

        String fileNameOnly = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);

        fileUrl = request.getScheme() + "://" 
                + request.getServerName() + ":" 
                + request.getServerPort()
                + request.getContextPath()
                + "/uploads/" + fileNameOnly;
    }
%>

<script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.min.js"></script>

<%@ include file="/header.jsp" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/convert.css">

<div class="main">

    <% if (fileNameObj != null && filePathObj != null) { %>

        <p class="file-label">
            <strong>Tên file:</strong> <%= fileName %>
        </p>

        <div class="pdf-preview">
            <canvas id="pdfThumb" width="300"></canvas>
        </div>

        <form action="convert" method="POST" class="convert-form">
            <input type="hidden" name="filePath" value="<%= filePathObj %>">
            <button type="submit" class="btn btn-primary">Convert sang Word</button>
        </form>

        <div class="action">
            <div class="text-center mt-3" style="margin-top: 20px;">
                <button type="button" class="btn-back" onclick="window.location.href='index.jsp'">
                    Quay lại
                </button>
            </div>
        </div>

    <% } else { %>

        <p class="error-text">
            Không có tệp nào được tải lên.<br>
            Vui lòng quay lại trang chính và thử lại.
        </p>

    <% } %>

</div>

<%@ include file="/footer.jsp" %>

<script>
    const pdfPath = "<%= fileUrl %>";

    pdfjsLib.getDocument(pdfPath).promise.then(function (pdf) {
        pdf.getPage(1).then(function (page) {

            var viewport = page.getViewport({ scale: 1 });
            var canvas = document.getElementById("pdfThumb");
            var context = canvas.getContext("2d");

            canvas.height = viewport.height;
            canvas.width = viewport.width;

            page.render({
                canvasContext: context,
                viewport: viewport
            });

        });
    }).catch(function (error) {
        console.error("PDF load error:", error);
    });
</script>
