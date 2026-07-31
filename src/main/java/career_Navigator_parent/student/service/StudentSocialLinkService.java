package career_Navigator_parent.student.service;

import career_Navigator_parent.student.dto.request.CreateStudentSocialLinkRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentSocialLinkRequest;
import career_Navigator_parent.student.dto.response.StudentSocialLinkResponse;

import java.util.List;

public interface StudentSocialLinkService {

    StudentSocialLinkResponse createSocialLink(
            CreateStudentSocialLinkRequest request
    );

    StudentSocialLinkResponse updateSocialLink(
            Long socialLinkId,
            UpdateStudentSocialLinkRequest request
    );

    StudentSocialLinkResponse getSocialLinkById(
            Long socialLinkId
    );

    List<StudentSocialLinkResponse> getAllSocialLinks();

    void deleteSocialLink(
            Long socialLinkId
    );

}