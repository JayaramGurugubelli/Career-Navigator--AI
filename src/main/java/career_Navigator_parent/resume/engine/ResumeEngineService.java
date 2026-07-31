package career_Navigator_parent.resume.engine;


import career_Navigator_parent.resume.view.ResumeView;


public interface ResumeEngineService {


    /**
     * Generate resume preview data
     *
     * @param resumeId resume identifier
     * @return ResumeView containing complete resume data
     */
    ResumeView previewResume(
            Long resumeId
    );



    /**
     * Generate PDF resume
     *
     * @param resumeId resume identifier
     * @return PDF byte array
     */
    byte[] generatePdf(
            Long resumeId
    );



    /**
     * Generate DOCX resume
     *
     * @param resumeId resume identifier
     * @return DOCX byte array
     */
    byte[] generateDocx(
            Long resumeId
    );


}