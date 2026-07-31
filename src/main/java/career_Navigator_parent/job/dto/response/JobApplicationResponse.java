package career_Navigator_parent.job.dto.response;

import career_Navigator_parent.shared.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationResponse {

    private Long id;

    private Long jobId;
    private String jobTitle;
    private String jobSlug;

    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

    private Long studentId;

    private Long resumeId;
    private String resumeTitle;

    private String coverLetter;

    private ApplicationStatus status;

    private String recruiterNotes;

    private LocalDateTime appliedAt;
    private LocalDateTime lastStatusChangedAt;
    private LocalDateTime withdrawnAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}