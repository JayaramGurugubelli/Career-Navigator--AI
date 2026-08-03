package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.entity.Lesson;
import career_Navigator_parent.student.entity.Student;

public interface LearningContentAccessService {

    void validateLessonAccess(
            Student student,
            Lesson lesson
    );
}
