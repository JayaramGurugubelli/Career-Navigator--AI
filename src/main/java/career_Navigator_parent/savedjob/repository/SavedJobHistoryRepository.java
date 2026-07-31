package career_Navigator_parent.savedjob.repository;

import career_Navigator_parent.savedjob.entity.SavedJobHistory;
import career_Navigator_parent.savedjob.enums.SavedJobAction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface SavedJobHistoryRepository
        extends JpaRepository<SavedJobHistory, Long> {

    @Query("""
            SELECT history
            FROM SavedJobHistory history
            LEFT JOIN FETCH history.jobPosting job
            WHERE history.student.id = :studentId
              AND (
                    :action IS NULL
                    OR history.action = :action
                  )
            """)
    Page<SavedJobHistory> findStudentHistory(
            @Param("studentId")
            Long studentId,

            @Param("action")
            SavedJobAction action,

            Pageable pageable
    );
}