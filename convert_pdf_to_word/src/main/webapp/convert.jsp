<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Convert PDF</title>
</head>
<body>
<%
  Object fileNameObj = request.getAttribute("fileName");
  Object filePathObj = request.getAttribute("filePath");

  if (fileNameObj != null && filePathObj != null) {
    String fileName = fileNameObj.toString();
    String filePath = filePathObj.toString()
      .replace("\\", "/")
      .replace(request.getServletContext().getRealPath(""), request.getContextPath());
%>

    <h2>Chuyển đổi file</h2>
    <p><strong>Tên file:</strong> <%= fileName %></p>

    <div class="pdf-preview">
      <embed src="<%= filePath %>" type="application/pdf" width="100%" height="500px">
    </div>

    <form action="convert" method="POST">
      <input type="hidden" name="filePath" value="<%= filePathObj %>">
      <button type="submit">Convert sang Word</button>
    </form>

    <div class="text-center mt-3">
      <button type="button" class="btn btn-primary w-100 py-2 fw-semibold"
              onclick="window.location.href='index.jsp'">
        Quay lại
      </button>
    </div>

<%
  } else {
%>
    <p style="color:red;">Không có tệp nào được tải lên. Vui lòng quay lại trang chính và thử lại.</p>
<%
  }
%>
</body>
</html>
