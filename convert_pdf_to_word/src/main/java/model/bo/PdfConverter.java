package model.bo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import util.Utils;

public class PdfConverter {

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(Math.max(4, CORES * 2));
    public static void convertAsync(String pdfPath, Consumer<String> onComplete) {

        executor.submit(() -> {
            try {
                System.out.println("\n========== [DEBUG] CONVERT START ==========");
                System.out.println("[DEBUG] Input path: " + pdfPath);
                System.out.println("[DEBUG] Path is null? " + (pdfPath == null));
                if (pdfPath != null) {
                    java.io.File f = new java.io.File(pdfPath);
                    System.out.println("[DEBUG] File exists? " + f.exists());
                    System.out.println("[DEBUG] File size: " + (f.exists() ? f.length() + " bytes" : "N/A"));
                    System.out.println("[DEBUG] Absolute path: " + f.getAbsolutePath());
                }
                System.out.println("==========================================");

                System.out.println("[DEBUG] Using PdfConvertionHelper to split and combine...");
                PdfConvertionHelper.convertPdfToDoc(pdfPath);

                String outputPath = pdfPath.replace(".pdf", ".docx");
                System.out.println("[DEBUG] Convert success: " + outputPath);

                if (onComplete != null) {
                    System.out.println("[DEBUG] Running callback...");
                    onComplete.accept(outputPath);
                }

                System.out.println("========== [DEBUG] CONVERT END ==========\n");

            } catch (Exception e) {
                System.err.println("[DEBUG] Exception class: " + e.getClass().getName());
                System.err.println("[DEBUG] Message: " + e.getMessage());
                e.printStackTrace();
                try {
                    if (pdfPath != null) {
                        java.io.File f = new java.io.File(pdfPath);
                        System.err.println("[DEBUG] Exists after error? " + f.exists());
                        System.err.println("[DEBUG] Can read after error? " + f.canRead());
                        System.err.println("[DEBUG] Absolute path after error: " + f.getAbsolutePath());
                    }
                } catch (Exception inner) {
                    System.err.println("[DEBUG] Error checking file after exception: " + inner.getMessage());
                }
                Utils.deleteFile(pdfPath);
            }
        });
    }

    public static void shutdownExecutor() {
        executor.shutdownNow();
    }
}
