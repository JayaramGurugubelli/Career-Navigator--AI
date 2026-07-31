package career_Navigator_parent.student.mapper;

import career_Navigator_parent.student.dto.request.CreateStudentSocialLinkRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentSocialLinkRequest;
import career_Navigator_parent.student.dto.response.StudentSocialLinkResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentSocialLink;
import org.springframework.stereotype.Component;

@Component
public class StudentSocialLinkMapper {

    /**
     * Convert Create Request to Entity
     */
    public StudentSocialLink toEntity(
            CreateStudentSocialLinkRequest request,
            Student student
    ) {

        return StudentSocialLink.builder()
                .student(student)
                .platform(request.getPlatform())
                .url(request.getUrl())
                .displayOrder(request.getDisplayOrder())
                .visible(request.getVisible())
                .build();
    }


    /**
     * Convert Entity to Response
     */
    public StudentSocialLinkResponse toResponse(
            StudentSocialLink socialLink
    ) {

        return StudentSocialLinkResponse.builder()
                .id(socialLink.getId())
                .platform(socialLink.getPlatform())
                .url(socialLink.getUrl())
                .displayOrder(socialLink.getDisplayOrder())
                .visible(socialLink.getVisible())
                .build();
    }


    /**
     * Update Existing Entity
     */
    public void updateEntity(
            StudentSocialLink socialLink,
            UpdateStudentSocialLinkRequest request
    ) {

        socialLink.setUrl(
                request.getUrl()
        );

        socialLink.setDisplayOrder(
                request.getDisplayOrder()
        );

        socialLink.setVisible(
                request.getVisible()
        );

    }

}