package worker.util;

import java.util.ArrayList;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;

public class CombineDocx {

	public static void combineFiles(ArrayList<String> docFilePaths, String output) {
		Thread combineDocxThread = new Thread(() -> {
			Document firstDocument = new Document();
			firstDocument.loadFromFile(docFilePaths.get(0), FileFormat.Docx);

			for (int i = 1; i < docFilePaths.size(); i++) {
				Document documentMerge = new Document();
				documentMerge.loadFromFile(docFilePaths.get(i), FileFormat.Docx);

				// Merge files
				for (Object sectionObj : documentMerge.getSections()) {
					Section section = (Section) sectionObj;
					firstDocument.getSections().add(section.deepClone());
				}
			}

			firstDocument.saveToFile(output, FileFormat.Docx);
			System.out.println("[WORKER] Combine done");
		});

		combineDocxThread.start();
		try {
			combineDocxThread.join();
			deleteTemporalFiles(docFilePaths);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	private static void deleteTemporalFiles(ArrayList<String> temporalFiles) {
		for (String filePath : temporalFiles) {
			deleteFile(filePath);
			deleteFile(filePath.replace(".docx", ".pdf"));
		}
	}

	private static void deleteFile(String filePath) {
		try {
			java.io.File file = new java.io.File(filePath);
			if (file.exists()) {
				file.delete();
				System.out.println("[WORKER] Deleted temp file: " + filePath);
			}
		} catch (Exception e) {
			System.err.println("[WORKER] Failed to delete file: " + filePath);
		}
	}
}

