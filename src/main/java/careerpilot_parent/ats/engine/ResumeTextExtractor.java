package careerpilot_parent.ats.engine;

import org.apache.pdfbox.Loader;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class ResumeTextExtractor {

    public String extractText(
            Resource resource,
            String fileType
    ) {

        if (resource == null || !resource.exists()) {

            throw new IllegalArgumentException(
                    "Resume file does not exist."
            );
        }

        try {

            if (isPdf(fileType, resource.getFilename())) {

                return extractPdfText(resource);
            }

            if (isDocx(fileType, resource.getFilename())) {

                return extractDocxText(resource);
            }

            throw new IllegalArgumentException(
                    "Unsupported resume format. Only PDF and DOCX files are supported."
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to extract resume text: " +
                            exception.getMessage(),
                    exception
            );
        }
    }

    private String extractPdfText(Resource resource) throws Exception {

        try (
                InputStream inputStream =
                        resource.getInputStream();

                PDDocument document =
                        Loader.loadPDF(inputStream.readAllBytes())
        ) {

            PDFTextStripper textStripper =
                    new PDFTextStripper();

            return textStripper.getText(document);
        }
    }

    private String extractDocxText(
            Resource resource
    ) throws Exception {

        try (
                InputStream inputStream =
                        resource.getInputStream();

                XWPFDocument document =
                        new XWPFDocument(inputStream)
        ) {

            StringBuilder text =
                    new StringBuilder();

            document.getParagraphs()
                    .forEach(
                            paragraph -> text
                                    .append(paragraph.getText())
                                    .append(System.lineSeparator())
                    );

            document.getTables()
                    .forEach(
                            table -> table.getRows()
                                    .forEach(
                                            row -> row.getTableCells()
                                                    .forEach(
                                                            cell -> text
                                                                    .append(cell.getText())
                                                                    .append(" ")
                                                    )
                                    )
                    );

            return text.toString();
        }
    }

    private boolean isPdf(
            String fileType,
            String fileName
    ) {

        return "application/pdf".equalsIgnoreCase(fileType) ||
                hasExtension(fileName, ".pdf");
    }

    private boolean isDocx(
            String fileType,
            String fileName
    ) {

        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                .equalsIgnoreCase(fileType)
                || hasExtension(fileName, ".docx");
    }

    private boolean hasExtension(
            String fileName,
            String extension
    ) {

        return fileName != null &&
                fileName.toLowerCase()
                        .endsWith(extension);
    }
}