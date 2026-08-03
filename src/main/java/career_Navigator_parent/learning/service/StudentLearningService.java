package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.dto.request.StudentLearningRequests;
import career_Navigator_parent.learning.dto.response.LearningResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentLearningService {
    LearningResponses.Enrollment enroll(Long pathId);
    Page<LearningResponses.Enrollment> enrollments(Pageable pageable);
    LearningResponses.Lesson startLesson(Long lessonId);
    LearningResponses.Lesson updateLessonProgress(Long lessonId,StudentLearningRequests.LessonProgress r);
    LearningResponses.Lesson completeLesson(Long lessonId);
    LearningResponses.WeeklyGoal setWeeklyGoal(StudentLearningRequests.WeeklyGoal r);
}
