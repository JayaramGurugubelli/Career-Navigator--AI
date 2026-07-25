package careerpilot_parent.company.repository;

import careerpilot_parent.company.entity.Company;
import careerpilot_parent.company.enums.CompanyStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository
        extends JpaRepository<Company, Long> {

    boolean existsBySlug(String slug);

    boolean existsByOwnerId(Long ownerId);

    boolean existsByNameIgnoreCase(String name);

    Optional<Company> findBySlug(String slug);

    Optional<Company> findByOwnerId(Long ownerId);

    Optional<Company> findByIdAndOwnerId(
            Long companyId,
            Long ownerId
    );

    Optional<Company> findBySlugAndStatus(
            String slug,
            CompanyStatus status
    );

    @Query("""
            SELECT c
            FROM Company c
            JOIN FETCH c.owner
            WHERE c.owner.id = :ownerId
            """)
    Optional<Company> findByOwnerIdWithOwner(
            @Param("ownerId")
            Long ownerId
    );

    @Query("""
            SELECT c
            FROM Company c
            JOIN FETCH c.owner
            WHERE c.id = :companyId
            """)
    Optional<Company> findByIdWithOwner(
            @Param("companyId")
            Long companyId
    );
}