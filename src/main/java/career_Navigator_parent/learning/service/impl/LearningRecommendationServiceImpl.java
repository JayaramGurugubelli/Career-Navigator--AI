package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.entity.LearningPath;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.repository.LearningPathRepository;
import career_Navigator_parent.learning.service.LearningRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class LearningRecommendationServiceImpl implements LearningRecommendationService {
    private final LearningPathRepository paths;
    @Override public List<LearningResponses.Recommendation> recommendations(){
        List<LearningPath> list=paths.findByStatusAndActiveTrue(ContentStatus.PUBLISHED,PageRequest.of(0,10)).getContent();
        List<LearningResponses.Recommendation> out=new ArrayList<>();double score=100;
        for(LearningPath p:list){
            out.add(new LearningResponses.Recommendation(p.getId(),p.getTitle(),p.getCareerRole().getTitle(),
                    Boolean.TRUE.equals(p.getFeatured())?"Featured published path.":"Recommended from catalogue.",score));
            score=Math.max(50,score-5);
        }
        return out;
    }
}
