package career_Navigator_parent.resume.generator.service;


import career_Navigator_parent.resume.view.ResumeView;


public interface PdfResumeGenerator {


    byte[] generate(
            ResumeView resumeView
    );

}