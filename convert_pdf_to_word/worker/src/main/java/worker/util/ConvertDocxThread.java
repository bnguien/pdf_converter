package worker.util;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

public class ConvertDocxThread extends Thread {
    private final CountDownLatch latch;
    private final ArrayList<String> docFilePaths;
    private final String filePath;

    public ConvertDocxThread(ArrayList<String> docFilePaths, String filePath, CountDownLatch latch) {
        this.docFilePaths = docFilePaths;
        this.filePath = filePath;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println("[WORKER] Converting: " + filePath);
            String docFilePath = filePath.replace(".pdf", ".docx");
            boolean success = convertWithSpire(filePath, docFilePath);

            if (!success) {
                System.out.println("[WORKER] Fallback to PDFBox for: " + filePath);
                PdfBoxConverter.convertWithPdfBox(filePath, docFilePath);
            }

            synchronized (docFilePaths) {
                docFilePaths.add(docFilePath);
            }
        } catch (Exception e) {
            System.err.println("[WORKER] Convert failed: " + filePath + " - " + e.getMessage());
        } finally {
            latch.countDown();
        }
    }

    private boolean convertWithSpire(String input, String output) {
        try {
            PdfDocument doc = new PdfDocument();
            doc.loadFromFile(input);
            doc.saveToFile(output, FileFormat.DOCX);
            doc.close();
            return true;
        } catch (Exception e) {
            System.err.println("[WORKER] Spire ERROR: " + e.getMessage());
            return false;
        }
    }
}

