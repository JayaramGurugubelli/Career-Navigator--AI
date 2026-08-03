package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.StudentWeeklyLearningGoal;
import career_Navigator_parent.learning.enums.LearningGoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentWeeklyLearningGoalRepository
        extends JpaRepository<StudentWeeklyLearningGoal, Long> {

    Optional<StudentWeeklyLearningGoal>
    findByStudentIdAndWeekStartDate(
            Long studentId,
            LocalDate weekStartDate
    );

    List<StudentWeeklyLearningGoal>
    findByStudentIdOrderByWeekStartDateDesc(
            Long studentId
    );

    List<StudentWeeklyLearningGoal>
    findByStatusAndWeekEndDateBefore(
            LearningGoalStatus status,
            LocalDate date
    );
}