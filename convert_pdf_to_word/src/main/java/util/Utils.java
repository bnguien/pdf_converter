package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

	public static void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileName) {
		try {
			String fileNameOutput = fileName.substring(fileName.indexOf("_") + 1);
			System.out.println("Output:" + fileNameOutput);
			System.out.println("Downloading: " + fileName);

			String filePath = request.getServletContext().getRealPath("/upload") + "/" + fileName;
			String mimeType = request.getServletContext().getMimeType(fileName);

			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameOutput + "\"");
			try (FileInputStream fileInputStream = new FileInputStream(filePath);
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
		} catch (Exception e) {
			System.out.println(e.getMessage());
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