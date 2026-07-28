package careerpilot_parent.jobrecommendation.dto.response;

import careerpilot_parent.jobrecommendation.enums.RecommendationSource;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRecommendationResponse {

    private Long recommendationId;

    private Long jobId;
    private String jobTitle;
    private String jobSlug;

    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

    private String location;
    private String employmentType;
    private String workMode;
    private String experienceLevel;

    private Integer minimumExperience;
    private Integer maximumExperience;

    private LocalDate applicationDeadline;

    private String jobStatus;

    private Double matchScore;

    @Builder.Default
    private List<String> matchedSkills =
            new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills =
            new ArrayList<>();

    @Builder.Default
    private List<RecommendationReasonResponse> reasons =
            new ArrayList<>();

    private RecommendationSource source;

    private boolean saved;

    private boolean applied;

    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
}