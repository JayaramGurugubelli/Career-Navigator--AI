package careerpilot_parent.assessment.service.impl;

import careerpilot_parent.assessment.dto.response.AssessmentResponse;
import careerpilot_parent.assessment.entity.Assessment;
import careerpilot_parent.assessment.enums.AssessmentMode;
import careerpilot_parent.assessment.enums.AssessmentStatus;
import careerpilot_parent.assessment.mapper.AssessmentMapper;
import careerpilot_parent.assessment.repository.AssessmentRepository;
import careerpilot_parent.assessment.service.StudentAssessmentService;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.student.entity.Student;
import careerpilot_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentAssessmentServiceImpl
        implements StudentAssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final StudentRepository studentRepository;
    private final AssessmentMapper assessmentMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentResponse> getMyAssessments(
            AssessmentStatus status,
            Pageable pageable
    ) {

        Student student =
                getCurrentStudent();

        Page<Assessment> assessments;

        if (status == null) {

            assessments =
                    assessmentRepository
                            .findByJobApplicationStudentId(
                                    student.getId(),
                                    pageable
                            );

        } else {

            assessments =
                    assessmentRepository
                            .findByJobApplicationStudentIdAndStatus(
                                    student.getId(),
                                    status,
                                    pageable
                            );
        }

        return assessments.map(
                assessmentMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentResponse getAssessmentById(
            Long assessmentId
    ) {

        Student student =
                getCurrentStudent();

        Assessment assessment =
                getStudentAssessment(
                        assessmentId,
                        student.getId()
                );

        return assessmentMapper.toResponse(
                assessment
        );
    }

    @Override
    public AssessmentResponse startAssessment(
            Long assessmentId
    ) {

        Student student =
                getCurrentStudent();

        Assessment assessment =
                getStudentAssessment(
                        assessmentId,
                        student.getId()
                );

        LocalDateTime now =
                LocalDateTime.now();

        if (assessment.getStatus()
                == AssessmentStatus.STARTED) {

            throw new IllegalStateException(
                    "Assessment has already been started."
            );
        }

        if (assessment.getStatus()
                == AssessmentStatus.COMPLETED
                || assessment.getStatus()
                == AssessmentStatus.SUBMITTED) {

            throw new IllegalStateException(
                    "Completed or submitted assessment cannot be started again."
            );
        }

        if (assessment.getStatus()
                == AssessmentStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled assessment cannot be started."
            );
        }

        if (assessment.getStatus()
                == AssessmentStatus.EXPIRED) {

            throw new IllegalStateException(
                    "Expired assessment cannot be started."
            );
        }

        if (assessment.getStatus()
                == AssessmentStatus.MISSED) {

            throw new IllegalStateException(
                    "Missed assessment cannot be started."
            );
        }

        if (now.isBefore(
                assessment.getScheduledAt()
        )) {

            throw new IllegalStateException(
                    "Assessment is not available yet."
            );
        }

        if (now.isAfter(
                assessment.getAvailableUntil()
        )) {

            assessment.setStatus(
                    AssessmentStatus.EXPIRED
            );

            assessmentRepository.save(assessment);

            throw new IllegalStateException(
                    "Assessment availability period has expired."
            );
        }

        /*
         * For an external assessment, the frontend uses
         * assessmentUrl from the response to redirect the user.
         */
        if (assessment.getAssessmentMode()
                == AssessmentMode.EXTERNAL_LINK
                && (assessment.getAssessmentUrl() == null
                || assessment.getAssessmentUrl().isBlank())) {

            throw new IllegalStateException(
                    "External assessment URL is unavailable."
            );
        }

        assessment.setStatus(
                AssessmentStatus.STARTED
        );

        assessment.setStartedAt(now);

        Assessment savedAssessment =
                assessmentRepository.save(assessment);

        return assessmentMapper.toResponse(
                savedAssessment
        );
    }

    @Override
    public AssessmentResponse submitAssessment(
            Long assessmentId
    ) {

        Student student =
                getCurrentStudent();

        Assessment assessment =
                getStudentAssessment(
                        assessmentId,
                        student.getId()
                );

        if (assessment.getStatus()
                != AssessmentStatus.STARTED) {

            throw new IllegalStateException(
                    "Only a started assessment can be submitted."
            );
        }

        assessment.setStatus(
                AssessmentStatus.SUBMITTED
        );

        assessment.setSubmittedAt(
                LocalDateTime.now()
        );

        Assessment savedAssessment =
                assessmentRepository.save(assessment);

        return assessmentMapper.toResponse(
                savedAssessment
        );
    }

    private Student getCurrentStudent() {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(currentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found."
                        )
                );
    }

    private Assessment getStudentAssessment(
            Long assessmentId,
            Long studentId
    ) {

        return assessmentRepository
                .findByIdAndJobApplicationStudentId(
                        assessmentId,
                        studentId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assessment not found or does not belong to the current student."
                        )
                );
    }
}