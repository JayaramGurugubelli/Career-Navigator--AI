package career_Navigator_parent.resume.engine;


import career_Navigator_parent.resume.generator.service.DocxResumeGenerator;
import career_Navigator_parent.resume.generator.service.PdfResumeGenerator;
import career_Navigator_parent.resume.view.ResumeView;
import career_Navigator_parent.resume.assembler.ResumeAssemblerService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeEngineServiceImpl implements ResumeEngineService {



    private final ResumeAssemblerService resumeAssemblerService;


    private final PdfResumeGenerator pdfResumeGenerator;


    private final DocxResumeGenerator docxResumeGenerator;





    /**
     * Preview complete resume data
     */
    @Override
    public ResumeView previewResume(Long resumeId) {


        return resumeAssemblerService.assembleResume(resumeId);

    }





    /**
     * Generate PDF Resume
     */
    @Override
    public byte[] generatePdf(Long resumeId) {


        ResumeView resumeView = resumeAssemblerService.assembleResume(resumeId);



        return pdfResumeGenerator.generate(resumeView);

    }





    /**
     * Generate DOCX Resume
     */
    @Override
    public byte[] generateDocx(Long resumeId) {


        ResumeView resumeView = resumeAssemblerService.assembleResume(resumeId);



        return docxResumeGenerator.generate(resumeView);

    }


}