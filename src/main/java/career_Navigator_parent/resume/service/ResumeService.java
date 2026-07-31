package career_Navigator_parent.resume.service;

import career_Navigator_parent.resume.dto.request.CreateResumeRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeRequest;
import career_Navigator_parent.resume.dto.response.ResumeResponse;

import java.util.List;


public interface ResumeService {


    ResumeResponse createResume(
            CreateResumeRequest request
    );


    ResumeResponse updateResume(
            Long resumeId,
            UpdateResumeRequest request
    );


    ResumeResponse getResumeById(
            Long resumeId
    );


    List<ResumeResponse> getAllResumes();


    void deleteResume(
            Long resumeId
    );

}
