package career_Navigator_parent.learning.repository;

import career_Navigator_parent.learning.entity.AssessmentOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentOptionRepository
        extends JpaRepository<AssessmentOption, Long> {

    List<AssessmentOption>
    findByQuestionIdAndActiveTrueOrderBySequenceNumberAsc(
            Long questionId
    );

    boolean existsByQuestionIdAndSequenceNumber(
            Long questionId,
            Integer sequenceNumber
    );

    long countByQuestionIdAndCorrectOptionTrueAndActiveTrue(
            Long questionId
    );
}