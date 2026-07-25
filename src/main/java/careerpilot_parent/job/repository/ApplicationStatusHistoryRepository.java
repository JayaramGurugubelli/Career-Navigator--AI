package careerpilot_parent.job.repository;

import careerpilot_parent.job.entity.ApplicationStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationStatusHistoryRepository
        extends JpaRepository<
        ApplicationStatusHistory,
        Long
        > {

    List<ApplicationStatusHistory>
    findByApplicationIdOrderByCreatedAtAsc(
            Long applicationId
    );
}