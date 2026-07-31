package career_Navigator_parent.student.service;


import career_Navigator_parent.student.dto.request.CreateStudentAchievementRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentAchievementRequest;
import career_Navigator_parent.student.dto.response.StudentAchievementResponse;

import java.util.List;


public interface StudentAchievementService {


    /**
     * Create Student Achievement
     */
    StudentAchievementResponse createAchievement(CreateStudentAchievementRequest request);



    /**
     * Update Student Achievement
     */
    StudentAchievementResponse updateAchievement(Long achievementId, UpdateStudentAchievementRequest request);



    /**
     * Get Achievement By Id
     */
    StudentAchievementResponse getAchievementById(Long achievementId);



    /**
     * Get All Achievements Of Logged In Student
     */
    List<StudentAchievementResponse> getAllAchievements();



    /**
     * Delete Achievement
     */
    void deleteAchievement(Long achievementId);

}