package careerpilot_parent.interview.mapper;

import careerpilot_parent.interview.dto.request.CreateInterviewRequest;
import careerpilot_parent.interview.dto.request.UpdateInterviewRequest;
import careerpilot_parent.interview.dto.response.InterviewResponse;
import careerpilot_parent.interview.entity.Interview;
import careerpilot_parent.interview.enums.InterviewResult;
import careerpilot_parent.interview.enums.InterviewStatus;
import org.springframework.stereotype.Component;

@Component
public class InterviewMapper {

    public Interview toEntity(
            CreateInterviewRequest request
    ) {

        return Interview.builder()
                .title(
                        normalizeText(
                                request.getTitle()
                        )
                )
                .description(
                        normalizeText(
                                request.getDescription()
                        )
                )
                .interviewType(
                        request.getInterviewType()
                )
                .interviewMode(
                        request.getInterviewMode()
                )
                .roundNumber(
                        request.getRoundNumber()
                )
                .scheduledAt(
                        request.getScheduledAt()
                )
                .endAt(
                        request.getEndAt()
                )
                .durationMinutes(
                        request.getDurationMinutes()
                )
                .meetingUrl(
                        normalizeText(
                                request.getMeetingUrl()
                        )
                )
                .meetingId(
                        normalizeText(
                                request.getMeetingId()
                        )
                )
                .meetingPassword(
                        normalizeText(
                                request.getMeetingPassword()
                        )
                )
                .location(
                        normalizeText(
                                request.getLocation()
                        )
                )
                .interviewerName(
                        normalizeText(
                                request.getInterviewerName()
                        )
                )
                .interviewerEmail(
                        normalizeText(
                                request.getInterviewerEmail()
                        )
                )
                .interviewerDesignation(
                        normalizeText(
                                request.getInterviewerDesignation()
                        )
                )
                .instructions(
                        normalizeText(
                                request.getInstructions()
                        )
                )
                .status(
                        InterviewStatus.SCHEDULED
                )
                .result(
                        InterviewResult.PENDING
                )
                .build();
    }

    public void updateEntity(
            Interview interview,
            UpdateInterviewRequest request
    ) {

        interview.setTitle(
                normalizeText(
                        request.getTitle()
                )
        );

        interview.setDescription(
                normalizeText(
                        request.getDescription()
                )
        );

        interview.setInterviewType(
                request.getInterviewType()
        );

        interview.setInterviewMode(
                request.getInterviewMode()
        );

        interview.setRoundNumber(
                request.getRoundNumber()
        );

        interview.setScheduledAt(
                request.getScheduledAt()
        );

        interview.setEndAt(
                request.getEndAt()
        );

        interview.setDurationMinutes(
                request.getDurationMinutes()
        );

        interview.setMeetingUrl(
                normalizeText(
                        request.getMeetingUrl()
                )
        );

        interview.setMeetingId(
                normalizeText(
                        request.getMeetingId()
                )
        );

        interview.setMeetingPassword(
                normalizeText(
                        request.getMeetingPassword()
                )
        );

        interview.setLocation(
                normalizeText(
                        request.getLocation()
                )
        );

        interview.setInterviewerName(
                normalizeText(
                        request.getInterviewerName()
                )
        );

        interview.setInterviewerEmail(
                normalizeText(
                        request.getInterviewerEmail()
                )
        );

        interview.setInterviewerDesignation(
                normalizeText(
                        request.getInterviewerDesignation()
                )
        );

        interview.setInstructions(
                normalizeText(
                        request.getInstructions()
                )
        );
    }

    public InterviewResponse toResponse(
            Interview interview
    ) {

        var application =
                interview.getJobApplication();

        var job =
                application.getJobPosting();

        var company =
                job.getCompany();

        var student =
                application.getStudent();

        var studentUser =
                student.getUser();

        var recruiter =
                interview.getRecruiter();

        var recruiterUser =
                recruiter.getUser();

        return InterviewResponse.builder()
                .id(
                        interview.getId()
                )
                .applicationId(
                        application.getId()
                )
                .jobId(
                        job.getId()
                )
                .jobTitle(
                        job.getTitle()
                )
                .companyId(
                        company.getId()
                )
                .companyName(
                        company.getName()
                )
                .studentId(
                        student.getId()
                )
                .studentName(
                        buildFullName(
                                studentUser.getFirstName(),
                                studentUser.getLastName()
                        )
                )
                .recruiterId(
                        recruiter.getId()
                )
                .recruiterName(
                        buildFullName(
                                recruiterUser.getFirstName(),
                                recruiterUser.getLastName()
                        )
                )
                .title(
                        interview.getTitle()
                )
                .description(
                        interview.getDescription()
                )
                .interviewType(
                        interview.getInterviewType()
                )
                .interviewMode(
                        interview.getInterviewMode()
                )
                .roundNumber(
                        interview.getRoundNumber()
                )
                .scheduledAt(
                        interview.getScheduledAt()
                )
                .endAt(
                        interview.getEndAt()
                )
                .durationMinutes(
                        interview.getDurationMinutes()
                )
                .meetingUrl(
                        interview.getMeetingUrl()
                )
                .meetingId(
                        interview.getMeetingId()
                )
                .meetingPassword(
                        interview.getMeetingPassword()
                )
                .location(
                        interview.getLocation()
                )
                .interviewerName(
                        interview.getInterviewerName()
                )
                .interviewerEmail(
                        interview.getInterviewerEmail()
                )
                .interviewerDesignation(
                        interview.getInterviewerDesignation()
                )
                .instructions(
                        interview.getInstructions()
                )
                .status(
                        interview.getStatus()
                )
                .result(
                        interview.getResult()
                )
                .studentResponseNotes(
                        interview.getStudentResponseNotes()
                )
                .feedback(
                        interview.getFeedback()
                )
                .strengths(
                        interview.getStrengths()
                )
                .areasForImprovement(
                        interview.getAreasForImprovement()
                )
                .technicalScore(
                        interview.getTechnicalScore()
                )
                .communicationScore(
                        interview.getCommunicationScore()
                )
                .problemSolvingScore(
                        interview.getProblemSolvingScore()
                )
                .overallScore(
                        interview.getOverallScore()
                )
                .confirmedAt(
                        interview.getConfirmedAt()
                )
                .declinedAt(
                        interview.getDeclinedAt()
                )
                .completedAt(
                        interview.getCompletedAt()
                )
                .cancelledAt(
                        interview.getCancelledAt()
                )
                .createdAt(
                        interview.getCreatedAt()
                )
                .updatedAt(
                        interview.getUpdatedAt()
                )
                .build();
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

    private String buildFullName(
            String firstName,
            String lastName
    ) {

        String first =
                firstName == null
                        ? ""
                        : firstName.trim();

        String last =
                lastName == null
                        ? ""
                        : lastName.trim();

        return (
                first + " " + last
        ).trim();
    }
}