package worker.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import org.apache.pdfbox.pdmodel.PDDocument;
import worker.util.CombineDocx;
import worker.util.ConvertDocxThread;

public class PdfConvertionHelper {
    private static final int MAX_PAGES_PER_FILE = 3;

    public static void convertPdfToDoc(String fileInput) throws Exception {
        File f = new File(fileInput);
        System.out.println("[WORKER] File input: " + f.getAbsolutePath());
        System.out.println("[WORKER] File exists? " + f.exists());
        if (!f.exists()) {
            throw new Exception("File not found: " + fileInput);
        }

        String fileOutput = fileInput.replace(".pdf", ".docx");
        System.out.println("[WORKER] Starting split/convert: " + f.getAbsolutePath());
        convertPdfToDoc(fileInput, fileOutput);
    }

    private static void convertPdfToDoc(String fileInput, String fileOutput) throws Exception {
        System.out.println("[WORKER] Begin convertPdfToDoc");
        ArrayList<String> chunkFiles = splitPdf(fileInput);
        System.out.println("[WORKER] Chunk files created: " + chunkFiles.size());
        
        ArrayList<String> docFiles = convertChunkPdfToDocx(chunkFiles);
        System.out.println("[WORKER] DOCX parts generated: " + docFiles.size());
        
        Collections.sort(docFiles);
        System.out.println("[WORKER] Combining DOCX files...");
        CombineDocx.combineFiles(docFiles, fileOutput);
        System.out.println("[WORKER] Combined DOCX saved: " + fileOutput);
    }

    private static ArrayList<String> splitPdf(String filePath) throws Exception {
        ArrayList<String> pathOfChunkFiles = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            String baseName = filePath.replace(".pdf", "").replaceAll(" ", "");
            int totalPages = document.getNumberOfPages();
            System.out.println("[WORKER] Total pages in PDF: " + totalPages);
            int fileIndex = 1;

            for (int start = 0; start < totalPages; start += MAX_PAGES_PER_FILE) {
                int end = Math.min(start + MAX_PAGES_PER_FILE, totalPages);
                System.out.println("[WORKER] Creating part " + fileIndex + " with pages " + (start + 1) + " to " + end);
                PDDocument chunk = new PDDocument();
                for (int i = start; i < end; i++) {
                    chunk.addPage(document.getPage(i));
                }
                String chunkPath = baseName + "_part_" + fileIndex + ".pdf";
                chunk.save(chunkPath);
                chunk.close();
                pathOfChunkFiles.add(chunkPath);
                System.out.println("[WORKER] Saved part " + fileIndex + " -> " + chunkPath);
                fileIndex++;
            }
            System.out.println("[WORKER] Split PDF into " + pathOfChunkFiles.size() + " parts");
        }
        return pathOfChunkFiles;
    }

    private static ArrayList<String> convertChunkPdfToDocx(ArrayList<String> chunkFiles) throws Exception {
        CountDownLatch latch = new CountDownLatch(chunkFiles.size());
        ArrayList<String> docPaths = new ArrayList<>();
        System.out.println("[WORKER] Starting conversion threads for " + chunkFiles.size() + " chunks");

        for (String chunk : chunkFiles) {
            System.out.println("[WORKER] Creating thread for chunk: " + chunk);
            ConvertDocxThread thread = new ConvertDocxThread(docPaths, chunk, latch);
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception("Conversion interrupted", e);
        }
        System.out.println("[WORKER] All chunk conversions completed");
        return docPaths;
    }
}

