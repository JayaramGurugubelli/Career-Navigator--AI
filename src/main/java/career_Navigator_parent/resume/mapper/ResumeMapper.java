package career_Navigator_parent.resume.mapper;


import career_Navigator_parent.resume.dto.request.CreateResumeRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeRequest;
import career_Navigator_parent.resume.dto.response.ResumeResponse;
import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.student.entity.Student;

import org.springframework.stereotype.Component;


@Component
public class ResumeMapper {


    public Resume toEntity(
            CreateResumeRequest request,
            Student student
    ) {

        return Resume.builder()
                .student(student)
                .resumeTitle(request.getResumeTitle())
                .template(request.getTemplate())
                .defaultResume(request.getDefaultResume())
                .build();
    }



    public ResumeResponse toResponse(
            Resume resume
    ) {

        return ResumeResponse.builder()
                .id(resume.getId())
                .resumeTitle(resume.getResumeTitle())
                .template(resume.getTemplate())
                .defaultResume(resume.getDefaultResume())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }



    public void updateEntity(
            Resume resume,
            UpdateResumeRequest request
    ) {


        resume.setResumeTitle(
                request.getResumeTitle()
        );


        resume.setTemplate(
                request.getTemplate()
        );


        resume.setDefaultResume(
                request.getDefaultResume()
        );

    }

}