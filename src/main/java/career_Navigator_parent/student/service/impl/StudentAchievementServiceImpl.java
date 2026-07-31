package career_Navigator_parent.student.service.impl;


import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.dto.request.CreateStudentAchievementRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentAchievementRequest;
import career_Navigator_parent.student.dto.response.StudentAchievementResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentAchievements;
import career_Navigator_parent.student.mapper.StudentAchievementMapper;
import career_Navigator_parent.student.repository.StudentAchievementRepository;
import career_Navigator_parent.student.repository.StudentRepository;
import career_Navigator_parent.student.service.StudentAchievementService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class StudentAchievementServiceImpl
        implements StudentAchievementService {


    private final StudentAchievementRepository achievementRepository;

    private final StudentAchievementMapper achievementMapper;

    private final StudentRepository studentRepository;

    private final SecurityUtils securityUtils;

    @Override
    public StudentAchievementResponse createAchievement(CreateStudentAchievementRequest request) {

        Long userId = securityUtils.getCurrentUserId();

        Student student =
                studentRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student profile not found for user id : "
                                                + userId
                                )
                        );


        StudentAchievements achievement = achievementMapper.toEntity(request, student);


        StudentAchievements savedAchievement = achievementRepository.save(achievement);


        return achievementMapper.toResponse(savedAchievement);
    }





    @Override
    public StudentAchievementResponse updateAchievement(Long achievementId, UpdateStudentAchievementRequest request) {


        StudentAchievements achievement = achievementRepository.findById(achievementId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student achievement not found with id : "
                                                + achievementId
                                )
                        );


        achievementMapper.updateEntity(achievement, request);


        StudentAchievements updatedAchievement = achievementRepository.save(achievement);


        return achievementMapper.toResponse(updatedAchievement);
    }





    @Override
    @Transactional(readOnly = true)
    public StudentAchievementResponse getAchievementById(Long achievementId) {


        StudentAchievements achievement = achievementRepository.findById(achievementId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student achievement not found with id : "
                                                + achievementId
                                )
                        );


        return achievementMapper.toResponse(achievement);
    }





    @Override
    @Transactional(readOnly = true)
    public List<StudentAchievementResponse> getAllAchievements() {


        Long userId = securityUtils.getCurrentUserId();


        Student student = studentRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student profile not found for user id : "
                                                + userId
                                )
                        );


        return achievementRepository
                .findByStudentId(student.getId())
                .stream()
                .map(achievementMapper::toResponse)
                .toList();
    }





    @Override
    public void deleteAchievement(
            Long achievementId
    ) {


        StudentAchievements achievement =
                achievementRepository.findById(achievementId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student achievement not found with id : "
                                                + achievementId
                                )
                        );


        achievementRepository.delete(achievement);

    }

}