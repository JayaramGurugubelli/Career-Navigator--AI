package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import java.util.List;
public interface LearningRecommendationService {
    List<LearningResponses.Recommendation> recommendations();
}
