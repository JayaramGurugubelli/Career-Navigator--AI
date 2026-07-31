package career_Navigator_parent.student.service.impl;


import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.dto.request.CreateStudentCertificationRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentCertificationRequest;
import career_Navigator_parent.student.dto.response.StudentCertificationResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentCertification;
import career_Navigator_parent.student.mapper.StudentCertificationMapper;
import career_Navigator_parent.student.repository.StudentCertificationRepository;
import career_Navigator_parent.student.repository.StudentRepository;
import career_Navigator_parent.student.service.StudentCertificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class StudentCertificationServiceImpl
        implements StudentCertificationService {


    private final StudentCertificationRepository certificationRepository;

    private final StudentCertificationMapper certificationMapper;

    private final StudentRepository studentRepository;

    private final SecurityUtils securityUtils;

    @Override
    public StudentCertificationResponse createCertification(CreateStudentCertificationRequest request) {


        Long userId = securityUtils.getCurrentUserId();


        Student student = studentRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student profile not found for user id : "
                                                + userId
                                )
                        );


        StudentCertification certification = certificationMapper.toEntity(request, student);


        StudentCertification savedCertification = certificationRepository.save(certification);


        return certificationMapper.toResponse(savedCertification);
    }




    @Override
    public StudentCertificationResponse updateCertification(Long certificationId, UpdateStudentCertificationRequest request) {


        StudentCertification certification =
                certificationRepository.findById(certificationId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student certification not found with id : "
                                                + certificationId
                                )
                        );


        certificationMapper.updateEntity(certification, request);


        StudentCertification updatedCertification = certificationRepository.save(certification);


        return certificationMapper.toResponse(updatedCertification);
    }





    @Override
    @Transactional(readOnly = true)
    public StudentCertificationResponse getCertificationById(Long certificationId) {


        StudentCertification certification =
                certificationRepository.findById(certificationId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student certification not found with id : "
                                                + certificationId
                                )
                        );


        return certificationMapper.toResponse(certification);
    }





    @Override
    @Transactional(readOnly = true)
    public List<StudentCertificationResponse> getAllCertifications() {


        Long userId = securityUtils.getCurrentUserId();


        Student student =
                studentRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student profile not found for user id : "
                                                + userId
                                )
                        );


        return certificationRepository
                .findByStudentId(student.getId())
                .stream()
                .map(certificationMapper::toResponse)
                .toList();
    }





    @Override
    public void deleteCertification(Long certificationId) {


        StudentCertification certification = certificationRepository.findById(certificationId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student certification not found with id : "
                                                + certificationId
                                )
                        );


        certificationRepository.delete(certification);

    }

}