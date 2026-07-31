package career_Navigator_parent.offer.entity;

import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.offer.enums.OfferStatus;
import career_Navigator_parent.company.entity.RecruiterProfile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "job_offers",
        indexes = {
                @Index(
                        name = "idx_offer_application",
                        columnList = "application_id"
                ),
                @Index(
                        name = "idx_offer_recruiter",
                        columnList = "recruiter_id"
                ),
                @Index(
                        name = "idx_offer_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_offer_expiry",
                        columnList = "offer_expiry_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "application_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_offer_application"
            )
    )
    private JobApplication jobApplication;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "recruiter_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_offer_recruiter"
            )
    )
    private RecruiterProfile recruiter;

    @Column(
            name = "offer_title",
            nullable = false,
            length = 200
    )
    private String offerTitle;

    @Column(
            name = "annual_ctc",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal annualCtc;

    @Column(
            name = "base_salary",
            precision = 15,
            scale = 2
    )
    private BigDecimal baseSalary;

    @Column(
            name = "bonus",
            precision = 15,
            scale = 2
    )
    private BigDecimal bonus;

    @Column(
            nullable = false,
            length = 10
    )
    private String currency;

    @Column(
            name = "employment_type",
            nullable = false,
            length = 50
    )
    private String employmentType;

    @Column(
            name = "work_location",
            nullable = false,
            length = 200
    )
    private String workLocation;

    @Column(
            name = "joining_date",
            nullable = false
    )
    private LocalDate joiningDate;

    @Column(
            name = "offer_expiry_date",
            nullable = false
    )
    private LocalDate offerExpiryDate;

    @Column(name = "probation_period_months")
    private Integer probationPeriodMonths;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Lob
    @Column(
            name = "terms_and_conditions",
            columnDefinition = "TEXT"
    )
    private String termsAndConditions;

    @Column(
            name = "offer_letter_url",
            length = 1000
    )
    private String offerLetterUrl;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private OfferStatus status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Lob
    @Column(
            name = "student_response_notes",
            columnDefinition = "TEXT"
    )
    private String studentResponseNotes;

    @Lob
    @Column(
            name = "withdrawal_reason",
            columnDefinition = "TEXT"
    )
    private String withdrawalReason;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = OfferStatus.DRAFT;
        }

        if (currency == null || currency.isBlank()) {
            currency = "INR";
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}