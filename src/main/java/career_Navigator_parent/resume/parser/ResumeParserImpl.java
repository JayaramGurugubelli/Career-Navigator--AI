package career_Navigator_parent.resume.parser;

import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ResumeParserImpl implements ResumeParser {

    private final TextCleaner textCleaner;

    @Override
    public String extractText(File file) {

        if (file == null || !file.exists()) {
            throw new RuntimeException("Resume file not found.");
        }

        String fileName = file.getName().toLowerCase();

        try {

            if (fileName.endsWith(".pdf")) {
                return extractPdf(file);
            }

            if (fileName.endsWith(".docx")) {
                return extractDocx(file);
            }

            throw new RuntimeException(
                    "Unsupported file format : " + fileName
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to parse resume.",
                    e
            );

        }

    }

    private String extractPdf(File file)
            throws IOException {

        PDDocument document =
                Loader.loadPDF(file);

        PDFTextStripper stripper =
                new PDFTextStripper();

        String text =
                stripper.getText(document);

        document.close();

        return textCleaner.clean(text);

    }

    private String extractDocx(File file)
            throws IOException {

        FileInputStream inputStream =
                new FileInputStream(file);

        XWPFDocument document =
                new XWPFDocument(inputStream);

        XWPFWordExtractor extractor =
                new XWPFWordExtractor(document);

        String text =
                extractor.getText();

        extractor.close();
        document.close();
        inputStream.close();

        return textCleaner.clean(text);

    }

}