package career_Navigator_parent.resume.engine.controller;


import career_Navigator_parent.resume.assembler.ResumeAssemblerService;
import career_Navigator_parent.resume.generator.service.DocxResumeGenerator;
import career_Navigator_parent.resume.generator.service.PdfResumeGenerator;
import career_Navigator_parent.resume.view.ResumeView;


import lombok.RequiredArgsConstructor;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/student/resume-engine")
@RequiredArgsConstructor
public class ResumeEngineController {


    private final ResumeAssemblerService resumeAssemblerService;

    private final PdfResumeGenerator pdfResumeGenerator;

    private final DocxResumeGenerator docxResumeGenerator;





    /**
     * Resume Preview JSON
     */
    @GetMapping("/{resumeId}/preview")
    public ResponseEntity<ResumeView> previewResume(
            @PathVariable Long resumeId
    ){


        return ResponseEntity.ok(
                resumeAssemblerService
                        .assembleResume(resumeId)
        );

    }






    /**
     * Generate PDF Resume
     */
    @GetMapping("/{resumeId}/pdf")
    public ResponseEntity<ByteArrayResource> downloadPdf(
            @PathVariable Long resumeId
    ){


        ResumeView resumeView =
                resumeAssemblerService
                        .assembleResume(resumeId);



        byte[] pdfBytes =
                pdfResumeGenerator
                        .generate(resumeView);



        ByteArrayResource resource =
                new ByteArrayResource(pdfBytes);



        return ResponseEntity.ok()

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"resume.pdf\""
                )

                .contentLength(
                        pdfBytes.length
                )

                .body(resource);

    }









    /**
     * Generate DOCX Resume
     */
    @GetMapping("/{resumeId}/docx")
    public ResponseEntity<ByteArrayResource> downloadDocx(
            @PathVariable Long resumeId
    ){


        ResumeView resumeView =
                resumeAssemblerService
                        .assembleResume(resumeId);



        byte[] docxBytes =
                docxResumeGenerator
                        .generate(resumeView);



        ByteArrayResource resource =
                new ByteArrayResource(docxBytes);



        return ResponseEntity.ok()

                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )
                )


                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"resume.docx\""
                )


                .contentLength(
                        docxBytes.length
                )


                .body(resource);


    }



}