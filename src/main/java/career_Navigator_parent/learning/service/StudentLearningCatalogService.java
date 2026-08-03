package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudentLearningCatalogService {
    List<LearningResponses.Discipline> disciplines();
    Page<LearningResponses.Role> careerRoles(Long disciplineId,Pageable pageable);
    LearningResponses.Role careerRole(Long roleId);
    Page<LearningResponses.PathSummary> paths(Long disciplineId,Long roleId,Pageable pageable);
    LearningResponses.PathDetail path(Long pathId);
    LearningResponses.CourseDetail course(Long courseId);
}
