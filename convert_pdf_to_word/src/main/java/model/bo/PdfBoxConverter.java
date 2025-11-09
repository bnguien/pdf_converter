package model.bo;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class PdfBoxConverter {

    public static void convertWithPdfBox(String pdfPath, String outputPath) {
        try (PDDocument doc = PDDocument.load(new File(pdfPath));
             XWPFDocument wordDoc = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(outputPath)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            XWPFParagraph p = wordDoc.createParagraph();
            XWPFRun run = p.createRun();
            run.setText(text);
            run.setFontFamily("Times New Roman");
            run.setFontSize(12);

            wordDoc.write(out);
            System.out.println("[DEBUG] PDFBox fallback done: " + outputPath);
        } catch (Exception e) {
            System.err.println("[PDFBox ERROR] " + e.getMessage());
        }
    }
}
