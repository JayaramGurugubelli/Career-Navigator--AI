package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.response.StudentCodingResponses.*;
import career_Navigator_parent.coding.enums.ProblemAttemptStatus;
import career_Navigator_parent.coding.enums.ProblemDifficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StudentCodingActivityService {

    Dashboard dashboard();

    OverallProgress progress(
            LocalDate fromDate,
            LocalDate toDate
    );

    List<TopicProgress> topicProgress();

    List<DifficultyProgress> difficultyProgress();

    ActivityCalendar activityCalendar(
            LocalDate fromDate,
            LocalDate toDate
    );

    List<Recommendation> recommendations(
            ProblemDifficulty difficulty,
            int limit
    );

    Page<Attempt> attempts(
            ProblemAttemptStatus status,
            Pageable pageable
    );

    Bookmark addBookmark(Long problemId);

    void removeBookmark(Long problemId);
}