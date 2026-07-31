package career_Navigator_parent.offer.repository;

import career_Navigator_parent.offer.entity.JobOffer;
import career_Navigator_parent.offer.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface JobOfferRepository
        extends JpaRepository<JobOffer, Long> {

    Page<JobOffer> findByRecruiterId(
            Long recruiterId,
            Pageable pageable
    );

    Page<JobOffer> findByRecruiterIdAndStatus(
            Long recruiterId,
            OfferStatus status,
            Pageable pageable
    );

    Page<JobOffer>
    findByJobApplicationIdAndRecruiterId(
            Long applicationId,
            Long recruiterId,
            Pageable pageable
    );

    Optional<JobOffer> findByIdAndRecruiterId(
            Long offerId,
            Long recruiterId
    );

    Page<JobOffer>
    findByJobApplicationStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<JobOffer>
    findByJobApplicationStudentIdAndStatus(
            Long studentId,
            OfferStatus status,
            Pageable pageable
    );

    Optional<JobOffer>
    findByIdAndJobApplicationStudentId(
            Long offerId,
            Long studentId
    );

    boolean existsByJobApplicationIdAndStatusIn(
            Long applicationId,
            Collection<OfferStatus> statuses
    );
    long countByStatus(OfferStatus status);
}