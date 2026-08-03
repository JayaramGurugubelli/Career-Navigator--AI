package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.entity.Course;
import career_Navigator_parent.learning.entity.StudentCourseProgress;
import career_Navigator_parent.learning.entity.StudentLearningPathEnrollment;
import career_Navigator_parent.learning.entity.StudentWeeklyLearningGoal;
import career_Navigator_parent.learning.enums.AssessmentAttemptStatus;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.enums.EnrollmentStatus;
import career_Navigator_parent.learning.enums.ProgressStatus;
import career_Navigator_parent.learning.enums.ProjectSubmissionStatus;
import career_Navigator_parent.learning.mapper.LearningMapper;
import career_Navigator_parent.learning.repository.LearningCertificateRepository;
import career_Navigator_parent.learning.repository.StudentAssessmentAttemptRepository;
import career_Navigator_parent.learning.repository.StudentCourseProgressRepository;
import career_Navigator_parent.learning.repository.StudentLearningPathEnrollmentRepository;
import career_Navigator_parent.learning.repository.StudentLessonProgressRepository;
import career_Navigator_parent.learning.repository.StudentProjectSubmissionRepository;
import career_Navigator_parent.learning.repository.StudentWeeklyLearningGoalRepository;
import career_Navigator_parent.learning.service.LearningDashboardService;
import career_Navigator_parent.learning.service.LearningRecommendationService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningDashboardServiceImpl
        implements LearningDashboardService {

    private final StudentLearningPathEnrollmentRepository enrollmentRepository;
    private final StudentCourseProgressRepository courseProgressRepository;
    private final StudentLessonProgressRepository lessonProgressRepository;
    private final StudentAssessmentAttemptRepository assessmentAttemptRepository;
    private final StudentProjectSubmissionRepository projectSubmissionRepository;
    private final LearningCertificateRepository certificateRepository;
    private final StudentWeeklyLearningGoalRepository weeklyGoalRepository;
    private final StudentRepository studentRepository;
    private final SecurityUtils securityUtils;
    private final LearningMapper learningMapper;
    private final LearningRecommendationService recommendationService;

    @Override
    public LearningResponses.Dashboard dashboard() {
        Student student = currentStudent();

        StudentLearningPathEnrollment activeEnrollment =
                findActivePublishedEnrollment(student);

        List<StudentCourseProgress> recentPublishedProgress =
                courseProgressRepository
                        .findByStudentIdOrderByLastAccessedAtDesc(
                                student.getId(),
                                PageRequest.of(0, 20)
                        )
                        .stream()
                        .filter(progress ->
                                isPublishedActive(
                                        progress.getCourse()
                                )
                        )
                        .limit(5)
                        .toList();

        StudentCourseProgress currentProgress =
                recentPublishedProgress.stream()
                        .findFirst()
                        .orElse(null);

        LearningResponses.WeeklyGoal weeklyGoal =
                currentWeeklyGoal(student);

        long passedAssessments =
                assessmentAttemptRepository
                        .findByStudentIdOrderByStartedAtDesc(
                                student.getId(),
                                PageRequest.of(0, 1000)
                        )
                        .stream()
                        .filter(attempt ->
                                attempt.getStatus()
                                        == AssessmentAttemptStatus.PASSED
                        )
                        .count();

        long approvedProjects =
                projectSubmissionRepository
                        .findByStudentIdOrderBySubmittedAtDesc(
                                student.getId(),
                                PageRequest.of(0, 1000)
                        )
                        .stream()
                        .filter(submission ->
                                submission.getStatus()
                                        == ProjectSubmissionStatus.APPROVED
                        )
                        .count();

        List<LearningResponses.CourseSummary> recentCourses =
                recentPublishedProgress.stream()
                        .map(StudentCourseProgress::getCourse)
                        .map(learningMapper::toCourse)
                        .toList();

        return new LearningResponses.Dashboard(
                activeEnrollment == null
                        ? null
                        : learningMapper.toEnrollment(
                        activeEnrollment
                ),
                currentProgress == null
                        ? null
                        : learningMapper.toCourse(
                        currentProgress.getCourse()
                ),
                null,
                weeklyGoal,
                courseProgressRepository
                        .countByStudentIdAndStatus(
                                student.getId(),
                                ProgressStatus.COMPLETED
                        ),
                lessonProgressRepository
                        .countByStudentIdAndStatus(
                                student.getId(),
                                ProgressStatus.COMPLETED
                        ),
                passedAssessments,
                approvedProjects,
                certificateRepository
                        .findByStudentIdAndRevokedFalseOrderByIssuedAtDesc(
                                student.getId(),
                                PageRequest.of(0, 1)
                        )
                        .getTotalElements(),
                recentCourses,
                recommendationService.recommendations()
        );
    }

    private StudentLearningPathEnrollment findActivePublishedEnrollment(
            Student student
    ) {
        List<StudentLearningPathEnrollment> candidates =
                enrollmentRepository.findByStudentIdAndStatus(
                        student.getId(),
                        EnrollmentStatus.IN_PROGRESS
                );

        if (candidates.isEmpty()) {
            candidates =
                    enrollmentRepository.findByStudentIdAndStatus(
                            student.getId(),
                            EnrollmentStatus.ENROLLED
                    );
        }

        return candidates.stream()
                .filter(enrollment ->
                        enrollment.getLearningPath()
                                .getStatus()
                                == ContentStatus.PUBLISHED
                )
                .filter(enrollment ->
                        Boolean.TRUE.equals(
                                enrollment.getLearningPath()
                                        .getActive()
                        )
                )
                .findFirst()
                .orElse(null);
    }

    private LearningResponses.WeeklyGoal currentWeeklyGoal(
            Student student
    ) {
        LocalDate weekStart =
                LocalDate.now()
                        .with(
                                TemporalAdjusters
                                        .previousOrSame(
                                                DayOfWeek.MONDAY
                                        )
                        );

        StudentWeeklyLearningGoal goal =
                weeklyGoalRepository
                        .findByStudentIdAndWeekStartDate(
                                student.getId(),
                                weekStart
                        )
                        .orElse(null);

        if (goal == null) {
            return null;
        }

        return new LearningResponses.WeeklyGoal(
                goal.getId(),
                goal.getWeekStartDate(),
                goal.getWeekEndDate(),
                goal.getTargetMinutes(),
                goal.getCompletedMinutes(),
                goal.getTargetLessons(),
                goal.getCompletedLessons(),
                goal.getStatus()
        );
    }

    private boolean isPublishedActive(
            Course course
    ) {
        return course != null
                && course.getStatus()
                == ContentStatus.PUBLISHED
                && Boolean.TRUE.equals(
                course.getActive()
        );
    }

    private Student currentStudent() {
        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository.findByUserId(
                        userId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Student profile not found."
                        )
                );
    }
}
