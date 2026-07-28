package careerpilot_parent.jobrecommendation.mapper;

import careerpilot_parent.job.entity.JobPosting;

import careerpilot_parent.jobrecommendation.dto.response.JobRecommendationResponse;
import careerpilot_parent.jobrecommendation.dto.response.RecommendationReasonResponse;

import careerpilot_parent.jobrecommendation.entity.JobRecommendation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JobRecommendationMapper {

    public JobRecommendationResponse toResponse(
            JobRecommendation recommendation,
            boolean saved,
            boolean applied
    ) {

        if (recommendation == null) {
            return null;
        }

        JobPosting job =
                recommendation.getJobPosting();

        return JobRecommendationResponse.builder()
                .recommendationId(
                        recommendation.getId()
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
                .jobSlug(
                        job == null
                                ? null
                                : job.getSlug()
                )
                .companyId(
                        job == null ||
                                job.getCompany() == null
                                ? null
                                : job.getCompany().getId()
                )
                .companyName(
                        job == null ||
                                job.getCompany() == null
                                ? null
                                : job.getCompany().getName()
                )
                .companyLogoUrl(
                        job == null ||
                                job.getCompany() == null
                                ? null
                                : job.getCompany().getLogoUrl()
                )
                .location(
                        job == null
                                ? null
                                : job.getLocation()
                )
                .employmentType(
                        job == null ||
                                job.getEmploymentType() == null
                                ? null
                                : job.getEmploymentType().name()
                )
                .workMode(
                        job == null ||
                                job.getWorkMode() == null
                                ? null
                                : job.getWorkMode().name()
                )
                .experienceLevel(
                        job == null ||
                                job.getExperienceLevel() == null
                                ? null
                                : job.getExperienceLevel().name()
                )
                .minimumExperience(
                        job == null
                                ? null
                                : job.getMinimumExperience()
                )
                .maximumExperience(
                        job == null
                                ? null
                                : job.getMaximumExperience()
                )
                .applicationDeadline(
                        job == null
                                ? null
                                : job.getApplicationDeadline()
                )
                .jobStatus(
                        job == null ||
                                job.getStatus() == null
                                ? null
                                : job.getStatus().name()
                )
                .matchScore(
                        recommendation.getMatchScore()
                )
                .matchedSkills(
                        recommendation.getMatchedSkills() == null
                                ? new ArrayList<>()
                                : new ArrayList<>(
                                recommendation
                                        .getMatchedSkills()
                        )
                )
                .missingSkills(
                        recommendation.getMissingSkills() == null
                                ? new ArrayList<>()
                                : new ArrayList<>(
                                recommendation
                                        .getMissingSkills()
                        )
                )
                .reasons(
                        mapReasons(
                                recommendation.getReasons()
                        )
                )
                .source(recommendation.getSource())
                .saved(saved)
                .applied(applied)
                .generatedAt(
                        recommendation.getGeneratedAt()
                )
                .expiresAt(
                        recommendation.getExpiresAt()
                )
                .build();
    }

    private List<RecommendationReasonResponse> mapReasons(
            List<String> reasons
    ) {

        if (reasons == null || reasons.isEmpty()) {
            return new ArrayList<>();
        }

        return reasons.stream()
                .map(reason ->
                        RecommendationReasonResponse.builder()
                                .category(
                                        resolveCategory(reason)
                                )
                                .message(reason)
                                .scoreContribution(null)
                                .build()
                )
                .toList();
    }

    private String resolveCategory(
            String reason
    ) {

        if (reason == null) {
            return "GENERAL";
        }

        String normalized =
                reason.toLowerCase();

        if (normalized.contains("skill")) {
            return "SKILLS";
        }

        if (normalized.contains("experience")) {
            return "EXPERIENCE";
        }

        if (normalized.contains("saved")) {
            return "SAVED_JOB_BEHAVIOR";
        }

        if (normalized.contains("education")
                || normalized.contains("degree")
                || normalized.contains("branch")) {

            return "EDUCATION";
        }

        if (normalized.contains("recent")) {
            return "RECENCY";
        }

        return "GENERAL";
    }
}