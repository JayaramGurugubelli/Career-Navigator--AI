package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.entity.Course;
import career_Navigator_parent.learning.entity.Lesson;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.enums.EnrollmentStatus;
import career_Navigator_parent.learning.repository.StudentLearningPathEnrollmentRepository;
import career_Navigator_parent.learning.service.LearningContentAccessService;
import career_Navigator_parent.student.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningContentAccessServiceImpl
        implements LearningContentAccessService {

    private static final List<EnrollmentStatus> ACCESSIBLE_ENROLLMENT_STATUSES =
            List.of(
                    EnrollmentStatus.ENROLLED,
                    EnrollmentStatus.IN_PROGRESS,
                    EnrollmentStatus.PAUSED,
                    EnrollmentStatus.COMPLETED
            );

    private final StudentLearningPathEnrollmentRepository enrollmentRepository;

    @Override
    public void validateLessonAccess(
            Student student,
            Lesson lesson
    ) {
        if (student == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated student is required."
            );
        }

        if (lesson == null || !Boolean.TRUE.equals(lesson.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Active lesson not found."
            );
        }

        Course course = lesson.getModule().getCourse();

        if (course == null
                || course.getStatus() != ContentStatus.PUBLISHED
                || !Boolean.TRUE.equals(course.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Published course not found for this lesson."
            );
        }

        if (Boolean.TRUE.equals(lesson.getPreview())) {
            return;
        }

        boolean enrolled =
                enrollmentRepository.existsAccessibleEnrollmentForCourse(
                        student.getId(),
                        course.getId(),
                        ACCESSIBLE_ENROLLMENT_STATUSES
                );

        if (!enrolled) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Enroll in an active learning path before accessing this lesson."
            );
        }
    }
}
