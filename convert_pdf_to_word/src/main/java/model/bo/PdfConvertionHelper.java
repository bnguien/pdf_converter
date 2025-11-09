package model.bo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfConvertionHelper {
    private static final int MAX_PAGES_PER_FILE = 3;

    public static void convertPdfToDoc(String fileInput) {
        try {
            File f = new File(fileInput);
            System.out.println("[DEBUG] File input: " + f.getAbsolutePath());
            System.out.println("[DEBUG] File exists? " + f.exists());
            if (!f.exists()) return;

            String fileOutput = fileInput.replace(".pdf", ".docx");
            System.out.println("[DEBUG] Starting split/convert: " + f.getAbsolutePath());
            convertPdfToDoc(fileInput, fileOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void convertPdfToDoc(String fileInput, String fileOutput) {
        System.out.println("[DEBUG] Begin convertPdfToDoc");
        ArrayList<String> chunkFiles = splitPdf(fileInput);
        System.out.println("[DEBUG] Chunk files created: " + chunkFiles.size());
        for (String s : chunkFiles) {
            System.out.println("[DEBUG] Chunk file: " + s);
        }
        ArrayList<String> docFiles = convertChunkPdfToDocx(chunkFiles);
        System.out.println("[DEBUG] DOCX parts generated: " + docFiles.size());
        for (String d : docFiles) {
            System.out.println("[DEBUG] DOCX part: " + d);
        }
        Collections.sort(docFiles);
        System.out.println("[DEBUG] Combining DOCX files...");
        CombineDocx.combineFiles(docFiles, fileOutput);
        System.out.println("[DEBUG] Combined DOCX saved: " + fileOutput);
    }

    private static ArrayList<String> splitPdf(String filePath) {
        ArrayList<String> pathOfChunkFiles = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            String baseName = filePath.replace(".pdf", "").replaceAll(" ", "");
            int totalPages = document.getNumberOfPages();
            System.out.println("[DEBUG] Total pages in PDF: " + totalPages);
            int fileIndex = 1;

            for (int start = 0; start < totalPages; start += MAX_PAGES_PER_FILE) {
                int end = Math.min(start + MAX_PAGES_PER_FILE, totalPages);
                System.out.println("[DEBUG] Creating part " + fileIndex + " with pages " + (start + 1) + " to " + end);
                PDDocument chunk = new PDDocument();
                for (int i = start; i < end; i++) {
                    chunk.addPage(document.getPage(i));
                }
                String chunkPath = baseName + "_part_" + fileIndex + ".pdf";
                chunk.save(chunkPath);
                chunk.close();
                pathOfChunkFiles.add(chunkPath);
                System.out.println("[DEBUG] Saved part " + fileIndex + " -> " + chunkPath);
                fileIndex++;
            }
            System.out.println("[DEBUG] Split PDF into " + pathOfChunkFiles.size() + " parts");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pathOfChunkFiles;
    }

    private static ArrayList<String> convertChunkPdfToDocx(ArrayList<String> chunkFiles) {
        CountDownLatch latch = new CountDownLatch(chunkFiles.size());
        ArrayList<String> docPaths = new ArrayList<>();
        System.out.println("[DEBUG] Starting conversion threads for " + chunkFiles.size() + " chunks");

        for (String chunk : chunkFiles) {
            System.out.println("[DEBUG] Creating thread for chunk: " + chunk);
            ConvertDocxThread thread = new ConvertDocxThread(docPaths, chunk, latch);
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[DEBUG] All chunk conversions completed");
        return docPaths;
    }
}
