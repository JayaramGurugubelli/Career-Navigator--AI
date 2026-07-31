package career_Navigator_parent.assessment.dto.response;

import career_Navigator_parent.assessment.enums.AssessmentMode;
import career_Navigator_parent.assessment.enums.AssessmentProvider;
import career_Navigator_parent.assessment.enums.AssessmentResult;
import career_Navigator_parent.assessment.enums.AssessmentStatus;
import career_Navigator_parent.assessment.enums.AssessmentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResponse {

    private Long id;

    private Long applicationId;

    private Long jobId;
    private String jobTitle;

    private Long companyId;
    private String companyName;

    private Long studentId;
    private String studentName;

    private Long recruiterId;
    private String recruiterName;

    private String title;
    private String description;

    private AssessmentType assessmentType;
    private AssessmentMode assessmentMode;
    private AssessmentProvider provider;

    private String externalAssessmentId;
    private String assessmentUrl;

    private LocalDateTime scheduledAt;
    private LocalDateTime availableUntil;

    private Integer durationMinutes;

    private Double maximumScore;
    private Double passingScore;
    private Double obtainedScore;

    private AssessmentStatus status;
    private AssessmentResult result;

    private String instructions;
    private String resultNotes;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}