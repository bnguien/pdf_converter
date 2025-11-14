package controller;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet("/upload")
@MultipartConfig
public class UploadController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Part filePart = request.getPart("file");
        String folderUpload = request.getServletContext().getRealPath("/upload");
        if (folderUpload == null) {
            folderUpload = System.getProperty("java.io.tmpdir") + File.separator + "pdf_uploads";
        }

        Path uploadDir = Path.of(folderUpload);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String fileName = Path.of(filePart.getSubmittedFileName()).getFileName().toString();
        String filePath = uploadDir.resolve(System.currentTimeMillis() + "_" + fileName).toString();
        filePart.write(filePath);

        request.setAttribute("fileName", fileName);
        request.setAttribute("filePath", filePath);

        request.getSession().setAttribute("uploadedFilePath", filePath);
        request.getSession().setAttribute("uploadedFileName", fileName);
        request.getRequestDispatcher("convert.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

}