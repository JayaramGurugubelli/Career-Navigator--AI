package careerpilot_parent.job.dto.response;

import careerpilot_parent.company.enums.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private LocalDateTime appliedAt;

    private LocalDateTime lastStatusChangedAt;

    private LocalDateTime withdrawnAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}