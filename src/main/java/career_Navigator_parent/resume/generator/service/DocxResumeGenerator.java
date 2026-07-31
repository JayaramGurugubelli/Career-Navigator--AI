package career_Navigator_parent.resume.generator.service;


import career_Navigator_parent.resume.view.ResumeView;


public interface DocxResumeGenerator {


    byte[] generate(
            ResumeView resumeView
    );


}