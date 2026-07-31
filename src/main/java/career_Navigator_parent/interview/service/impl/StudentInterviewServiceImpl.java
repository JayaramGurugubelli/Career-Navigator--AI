package career_Navigator_parent.interview.service.impl;

import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.interview.dto.request.StudentInterviewResponseRequest;
import career_Navigator_parent.interview.dto.response.InterviewResponse;
import career_Navigator_parent.interview.entity.Interview;
import career_Navigator_parent.interview.enums.InterviewStatus;
import career_Navigator_parent.interview.mapper.InterviewMapper;
import career_Navigator_parent.interview.repository.InterviewRepository;
import career_Navigator_parent.interview.service.StudentInterviewService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentInterviewServiceImpl
        implements StudentInterviewService {

    private final InterviewRepository interviewRepository;

    private final StudentRepository studentRepository;

    private final InterviewMapper interviewMapper;

    private final SecurityUtils securityUtils;

    private static final Set<InterviewStatus>
            MISSED_ELIGIBLE_STATUSES =
            EnumSet.of(
                    InterviewStatus.SCHEDULED,
                    InterviewStatus.CONFIRMED,
                    InterviewStatus.RESCHEDULED
            );

    private static final Set<InterviewStatus>
            CONFIRMABLE_STATUSES =
            EnumSet.of(
                    InterviewStatus.SCHEDULED,
                    InterviewStatus.RESCHEDULED
            );

    private static final Set<InterviewStatus>
            DECLINABLE_STATUSES =
            EnumSet.of(
                    InterviewStatus.SCHEDULED,
                    InterviewStatus.CONFIRMED,
                    InterviewStatus.RESCHEDULED
            );

    @Override
    public Page<InterviewResponse> getMyInterviews(
            InterviewStatus status,
            Pageable pageable
    ) {

        Student student = getCurrentStudent();

        Page<Interview> interviews;

        if (status == null) {

            interviews =
                    interviewRepository
                            .findByJobApplicationStudentId(
                                    student.getId(),
                                    pageable
                            );

        } else {

            interviews =
                    interviewRepository
                            .findByJobApplicationStudentIdAndStatus(
                                    student.getId(),
                                    status,
                                    pageable
                            );
        }

        return interviews.map(interview -> {

            Interview refreshedInterview =
                    refreshTimeBasedStatus(interview);

            return interviewMapper.toResponse(
                    refreshedInterview
            );
        });
    }

    @Override
    public InterviewResponse getInterviewById(
            Long interviewId
    ) {

        Student student = getCurrentStudent();

        Interview interview =
                getStudentInterview(
                        interviewId,
                        student.getId()
                );

        interview = refreshTimeBasedStatus(interview);

        return interviewMapper.toResponse(interview);
    }

    @Override
    public InterviewResponse confirmInterview(
            Long interviewId,
            StudentInterviewResponseRequest request
    ) {

        Student student = getCurrentStudent();

        Interview interview =
                getStudentInterview(
                        interviewId,
                        student.getId()
                );

        interview = refreshTimeBasedStatus(interview);

        validateInterviewCanBeConfirmed(interview);

        LocalDateTime now = LocalDateTime.now();

        interview.setStatus(
                InterviewStatus.CONFIRMED
        );

        interview.setConfirmedAt(now);

        interview.setDeclinedAt(null);

        interview.setStudentResponseNotes(
                normalizeText(request.getNotes())
        );

        Interview savedInterview =
                interviewRepository.save(interview);

        return interviewMapper.toResponse(
                savedInterview
        );
    }

    @Override
    public InterviewResponse declineInterview(
            Long interviewId,
            StudentInterviewResponseRequest request
    ) {

        Student student = getCurrentStudent();

        Interview interview =
                getStudentInterview(
                        interviewId,
                        student.getId()
                );

        interview = refreshTimeBasedStatus(interview);

        validateInterviewCanBeDeclined(interview);

        LocalDateTime now = LocalDateTime.now();

        interview.setStatus(
                InterviewStatus.DECLINED
        );

        interview.setDeclinedAt(now);

        interview.setConfirmedAt(null);

        interview.setStudentResponseNotes(
                normalizeText(request.getNotes())
        );

        Interview savedInterview =
                interviewRepository.save(interview);

        return interviewMapper.toResponse(
                savedInterview
        );
    }

    private Student getCurrentStudent() {

        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Student profile not found."
                        )
                );
    }

    private Interview getStudentInterview(
            Long interviewId,
            Long studentId
    ) {

        return interviewRepository
                .findByIdAndJobApplicationStudentId(
                        interviewId,
                        studentId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Interview not found or does not belong to the current student."
                        )
                );
    }

    private void validateInterviewCanBeConfirmed(
            Interview interview
    ) {

        InterviewStatus currentStatus =
                interview.getStatus();

        if (currentStatus ==
                InterviewStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Interview has already been confirmed."
            );
        }

        if (currentStatus ==
                InterviewStatus.DECLINED) {

            throw new IllegalStateException(
                    "A declined interview cannot be confirmed. Contact the recruiter for rescheduling."
            );
        }

        if (currentStatus ==
                InterviewStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed interview cannot be confirmed."
            );
        }

        if (currentStatus ==
                InterviewStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled interview cannot be confirmed."
            );
        }

        if (currentStatus ==
                InterviewStatus.MISSED) {

            throw new IllegalStateException(
                    "Missed interview cannot be confirmed."
            );
        }

        if (!CONFIRMABLE_STATUSES.contains(
                currentStatus
        )) {

            throw new IllegalStateException(
                    "Interview cannot be confirmed while its status is "
                            + currentStatus
                            + "."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (!now.isBefore(
                interview.getScheduledAt()
        )) {

            throw new IllegalStateException(
                    "Interview confirmation time has passed."
            );
        }
    }

    private void validateInterviewCanBeDeclined(
            Interview interview
    ) {

        InterviewStatus currentStatus =
                interview.getStatus();

        if (currentStatus ==
                InterviewStatus.DECLINED) {

            throw new IllegalStateException(
                    "Interview has already been declined."
            );
        }

        if (currentStatus ==
                InterviewStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed interview cannot be declined."
            );
        }

        if (currentStatus ==
                InterviewStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled interview cannot be declined."
            );
        }

        if (currentStatus ==
                InterviewStatus.MISSED) {

            throw new IllegalStateException(
                    "Missed interview cannot be declined."
            );
        }

        if (!DECLINABLE_STATUSES.contains(
                currentStatus
        )) {

            throw new IllegalStateException(
                    "Interview cannot be declined while its status is "
                            + currentStatus
                            + "."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (!now.isBefore(
                interview.getScheduledAt()
        )) {

            throw new IllegalStateException(
                    "Interview cannot be declined after its scheduled start time."
            );
        }
    }

    private Interview refreshTimeBasedStatus(
            Interview interview
    ) {

        if (!MISSED_ELIGIBLE_STATUSES.contains(
                interview.getStatus()
        )) {

            return interview;
        }

        LocalDateTime now = LocalDateTime.now();

        /*
         * If the interview end time has passed and
         * the recruiter has not marked it completed,
         * the interview automatically becomes MISSED.
         */
        if (interview.getEndAt() != null &&
                !now.isBefore(
                        interview.getEndAt()
                )) {

            interview.setStatus(
                    InterviewStatus.MISSED
            );

            return interviewRepository.save(
                    interview
            );
        }

        return interview;
    }

    private String normalizeText(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}