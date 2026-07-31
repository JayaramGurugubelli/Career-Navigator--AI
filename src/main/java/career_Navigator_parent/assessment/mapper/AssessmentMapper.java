package career_Navigator_parent.assessment.mapper;

import career_Navigator_parent.assessment.dto.request.CreateAssessmentRequest;
import career_Navigator_parent.assessment.dto.request.UpdateAssessmentRequest;
import career_Navigator_parent.assessment.dto.response.AssessmentResponse;
import career_Navigator_parent.assessment.entity.Assessment;
import career_Navigator_parent.assessment.enums.AssessmentResult;
import career_Navigator_parent.assessment.enums.AssessmentStatus;
import career_Navigator_parent.company.entity.Company;
import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.job.entity.JobPosting;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AssessmentMapper {

    public Assessment toEntity(
            CreateAssessmentRequest request
    ) {

        if (request == null) {
            return null;
        }

        return Assessment.builder()
                .title(
                        normalizeRequiredText(
                                request.getTitle()
                        )
                )
                .description(
                        normalizeOptionalText(
                                request.getDescription()
                        )
                )
                .assessmentType(
                        request.getAssessmentType()
                )
                .assessmentMode(
                        request.getAssessmentMode()
                )
                .provider(
                        request.getProvider()
                )
                .externalAssessmentId(
                        normalizeOptionalText(
                                request.getExternalAssessmentId()
                        )
                )
                .assessmentUrl(
                        normalizeOptionalText(
                                request.getAssessmentUrl()
                        )
                )
                .scheduledAt(
                        request.getScheduledAt()
                )
                .availableUntil(
                        request.getAvailableUntil()
                )
                .durationMinutes(
                        request.getDurationMinutes()
                )
                .maximumScore(
                        request.getMaximumScore()
                )
                .passingScore(
                        request.getPassingScore()
                )
                .obtainedScore(null)
                .status(AssessmentStatus.SCHEDULED)
                .result(AssessmentResult.PENDING)
                .instructions(
                        normalizeOptionalText(
                                request.getInstructions()
                        )
                )
                .build();
    }

    public void updateEntity(
            UpdateAssessmentRequest request,
            Assessment assessment
    ) {

        if (request == null || assessment == null) {
            return;
        }

        assessment.setTitle(
                normalizeRequiredText(
                        request.getTitle()
                )
        );

        assessment.setDescription(
                normalizeOptionalText(
                        request.getDescription()
                )
        );

        assessment.setAssessmentType(
                request.getAssessmentType()
        );

        assessment.setProvider(
                request.getProvider()
        );

        assessment.setExternalAssessmentId(
                normalizeOptionalText(
                        request.getExternalAssessmentId()
                )
        );

        assessment.setAssessmentUrl(
                normalizeOptionalText(
                        request.getAssessmentUrl()
                )
        );

        assessment.setScheduledAt(
                request.getScheduledAt()
        );

        assessment.setAvailableUntil(
                request.getAvailableUntil()
        );

        assessment.setDurationMinutes(
                request.getDurationMinutes()
        );

        assessment.setMaximumScore(
                request.getMaximumScore()
        );

        assessment.setPassingScore(
                request.getPassingScore()
        );

        assessment.setInstructions(
                normalizeOptionalText(
                        request.getInstructions()
                )
        );
    }

    public AssessmentResponse toResponse(
            Assessment assessment
    ) {

        if (assessment == null) {
            return null;
        }

        JobApplication application =
                assessment.getJobApplication();

        JobPosting job =
                application == null
                        ? null
                        : application.getJobPosting();

        Company company =
                job == null
                        ? null
                        : job.getCompany();

        Student student =
                application == null
                        ? null
                        : application.getStudent();

        RecruiterProfile recruiter =
                assessment.getRecruiter();

        return AssessmentResponse.builder()
                .id(assessment.getId())
                .applicationId(
                        application == null
                                ? null
                                : application.getId()
                )
                .jobId(
                        job == null
                                ? null
                                : job.getId()
                )
                .jobTitle(
                        job == null
                                ? null
                                : job.getTitle()
                )
                .companyId(
                        company == null
                                ? null
                                : company.getId()
                )
                .companyName(
                        company == null
                                ? null
                                : company.getName()
                )
                .studentId(
                        student == null
                                ? null
                                : student.getId()
                )
                .studentName(
                        student == null
                                ? null
                                : buildUserName(
                                student.getUser()
                        )
                )
                .recruiterId(
                        recruiter == null
                                ? null
                                : recruiter.getId()
                )
                .recruiterName(
                        recruiter == null
                                ? null
                                : buildUserName(
                                recruiter.getUser()
                        )
                )
                .title(assessment.getTitle())
                .description(
                        assessment.getDescription()
                )
                .assessmentType(
                        assessment.getAssessmentType()
                )
                .assessmentMode(
                        assessment.getAssessmentMode()
                )
                .provider(
                        assessment.getProvider()
                )
                .externalAssessmentId(
                        assessment.getExternalAssessmentId()
                )
                .assessmentUrl(
                        assessment.getAssessmentUrl()
                )
                .scheduledAt(
                        assessment.getScheduledAt()
                )
                .availableUntil(
                        assessment.getAvailableUntil()
                )
                .durationMinutes(
                        assessment.getDurationMinutes()
                )
                .maximumScore(
                        assessment.getMaximumScore()
                )
                .passingScore(
                        assessment.getPassingScore()
                )
                .obtainedScore(
                        assessment.getObtainedScore()
                )
                .status(assessment.getStatus())
                .result(assessment.getResult())
                .instructions(
                        assessment.getInstructions()
                )
                .resultNotes(
                        assessment.getResultNotes()
                )
                .startedAt(
                        assessment.getStartedAt()
                )
                .submittedAt(
                        assessment.getSubmittedAt()
                )
                .completedAt(
                        assessment.getCompletedAt()
                )
                .cancelledAt(
                        assessment.getCancelledAt()
                )
                .createdAt(
                        assessment.getCreatedAt()
                )
                .updatedAt(
                        assessment.getUpdatedAt()
                )
                .build();
    }

    private String normalizeRequiredText(
            String value
    ) {

        return value == null
                ? null
                : value.trim();
    }

    private String normalizeOptionalText(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String buildUserName(
            User user
    ) {

        if (user == null) {
            return null;
        }

        String firstName =
                normalizeOptionalText(
                        user.getFirstName()
                );

        String lastName =
                normalizeOptionalText(
                        user.getLastName()
                );

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}