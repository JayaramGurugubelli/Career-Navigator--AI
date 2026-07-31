package career_Navigator_parent.jobrecommendation.service;

import career_Navigator_parent.jobrecommendation.dto.response.JobRecommendationResponse;
import career_Navigator_parent.jobrecommendation.enums.RecommendationSource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRecommendationService {

    Page<JobRecommendationResponse>
    getMyRecommendations(
            Double minimumScore,
            RecommendationSource source,
            Pageable pageable
    );

    JobRecommendationResponse
    getRecommendationByJobId(
            Long jobId
    );

    int refreshMyRecommendations();

    int refreshRecommendationsForStudent(
            Long studentId
    );

    int refreshAllActiveStudents();

    void dismissRecommendation(
            Long jobId
    );

    int deactivateExpiredRecommendations();

}