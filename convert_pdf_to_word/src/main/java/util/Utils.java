package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Utils {
	public static Date convertStringToDate(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Date date = new Date();
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static void deleteFile(String path) {
		if (path == null || path.isBlank())
			return;
		Path p = Path.of(path);
		for (int i = 0; i < 3; i++) {
			try {
				Files.deleteIfExists(p);
				System.out.println("[CLEANUP] Deleted: " + p);
				return;
			} catch (IOException e) {
				System.err.println("[CLEANUP] Retry " + (i + 1) + " failed: " + e.getMessage());
				try {
					Thread.sleep(300);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	/**
	 * Download file từ thư mục upload sử dụng đường dẫn từ biến môi trường
	 * @deprecated Method này không được sử dụng. Sử dụng DownloadController thay thế.
	 * File path nên được lấy từ database thông qua taskId.
	 */
	@Deprecated
	public static void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileName) {
		try {
			String fileNameOutput = fileName.substring(fileName.indexOf("_") + 1);
			System.out.println("Output:" + fileNameOutput);
			System.out.println("Downloading: " + fileName);

			// Sử dụng StoragePathUtil thay vì getRealPath để thống nhất với hệ thống
			String uploadDir = StoragePathUtil.getUploadDirectory();
			Path filePath = Paths.get(uploadDir, fileName);
			
			if (!Files.exists(filePath)) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + fileName);
				return;
			}
			
			String mimeType = request.getServletContext().getMimeType(fileName);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameOutput + "\"");
			
			try (InputStream fileInputStream = Files.newInputStream(filePath);
					OutputStream outputStream = response.getOutputStream()) {
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				long total = 0;
				while ((bytesRead = fileInputStream.read(buffer)) != -1) {
					total += bytesRead;
					outputStream.write(buffer, 0, bytesRead);
				}
				System.out.println("Tổng bytes:" + total);
			}
		} catch (IllegalStateException e) {
			System.err.println("[ERROR] " + e.getMessage());
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Cấu hình hệ thống chưa đúng. Vui lòng liên hệ quản trị viên.");
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("[ERROR] downloadFile error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static String sanitizeFileName(String input) {
		if (input == null) return "file";
		// Map special letters that are not decomposed by Normalizer (e.g. Đ/đ) to ASCII
		input = input.replace('\u0110', 'D').replace('\u0111', 'd');
		String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
		String noDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		
		String safe = noDiacritics.replaceAll("[\\\\/:*?\"<>|]", "_");
		safe = safe.replaceAll("\\s+", "_").replaceAll("_+", "_");
		
		safe = safe.trim();
		if (safe.length() > 120) safe = safe.substring(0, 120);
		if (safe.isEmpty()) safe = "file";
		return safe;
	}
}