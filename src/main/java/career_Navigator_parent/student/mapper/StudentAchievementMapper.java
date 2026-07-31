package career_Navigator_parent.student.mapper;


import career_Navigator_parent.student.dto.request.CreateStudentAchievementRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentAchievementRequest;
import career_Navigator_parent.student.dto.response.StudentAchievementResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentAchievements;

import org.springframework.stereotype.Component;


@Component
public class StudentAchievementMapper {


    /**
     * Convert Create Request DTO to Entity
     */
    public StudentAchievements toEntity(CreateStudentAchievementRequest request, Student student) {

        return StudentAchievements.builder()
                .student(student)
                .title(request.getTitle())
                .organization(request.getOrganization())
                .achievementDate(request.getAchievementDate())
                .description(request.getDescription())
                .certificateUrl(request.getCertificateUrl())
                .build();
    }



    /**
     * Convert Entity to Response DTO
     */
    public StudentAchievementResponse toResponse(StudentAchievements achievement) {

        return StudentAchievementResponse.builder()
                .id(achievement.getId())
                .title(achievement.getTitle())
                .organization(achievement.getOrganization())
                .achievementDate(achievement.getAchievementDate())
                .description(achievement.getDescription())
                .certificateUrl(achievement.getCertificateUrl())
                .build();
    }



    /**
     * Update Existing Entity
     */
    public void updateEntity(StudentAchievements achievement, UpdateStudentAchievementRequest request) {

        achievement.setTitle(request.getTitle());

        achievement.setOrganization(request.getOrganization());

        achievement.setAchievementDate(request.getAchievementDate());

        achievement.setDescription(request.getDescription());

        achievement.setCertificateUrl(request.getCertificateUrl());
    }

}