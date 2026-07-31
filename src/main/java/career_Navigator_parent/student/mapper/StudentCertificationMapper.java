package career_Navigator_parent.student.mapper;


import career_Navigator_parent.student.dto.request.CreateStudentCertificationRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentCertificationRequest;
import career_Navigator_parent.student.dto.response.StudentCertificationResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentCertification;

import org.springframework.stereotype.Component;


@Component
public class StudentCertificationMapper {


    /**
     * Convert Create Request DTO to Entity
     */
    public StudentCertification toEntity(CreateStudentCertificationRequest request, Student student) {

        return StudentCertification.builder()
                .student(student)
                .certificationName(request.getCertificationName())
                .issuingOrganization(request.getIssuingOrganization())
                .issueDate(request.getIssueDate())
                .expiryDate(request.getExpiryDate())
                .credentialId(request.getCredentialId())
                .credentialUrl(request.getCredentialUrl())
                .description(request.getDescription())
                .build();
    }



    /**
     * Convert Entity to Response DTO
     */
    public StudentCertificationResponse toResponse(StudentCertification certification) {

        return StudentCertificationResponse.builder()
                .id(certification.getId())
                .certificationName(certification.getCertificationName())
                .issuingOrganization(certification.getIssuingOrganization())
                .issueDate(certification.getIssueDate())
                .expiryDate(certification.getExpiryDate())
                .credentialId(certification.getCredentialId())
                .credentialUrl(certification.getCredentialUrl())
                .description(certification.getDescription())
                .build();
    }



    /**
     * Update Existing Entity
     */
    public void updateEntity(StudentCertification certification, UpdateStudentCertificationRequest request) {

        certification.setCertificationName(request.getCertificationName());

        certification.setIssuingOrganization(request.getIssuingOrganization());

        certification.setIssueDate(request.getIssueDate());

        certification.setExpiryDate(request.getExpiryDate());

        certification.setCredentialId(request.getCredentialId());

        certification.setCredentialUrl(request.getCredentialUrl());

        certification.setDescription(request.getDescription());
    }

}