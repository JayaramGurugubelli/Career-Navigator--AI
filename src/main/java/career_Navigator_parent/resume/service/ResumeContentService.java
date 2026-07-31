package career_Navigator_parent.resume.service;


import career_Navigator_parent.resume.dto.request.CreateResumeContentRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeContentRequest;
import career_Navigator_parent.resume.dto.response.ResumeContentResponse;


public interface ResumeContentService {


    ResumeContentResponse createContent(Long resumeId, CreateResumeContentRequest request);



    ResumeContentResponse updateContent(Long resumeId, UpdateResumeContentRequest request);



    ResumeContentResponse getContent(Long resumeId);



    void deleteContent(Long resumeId);

}