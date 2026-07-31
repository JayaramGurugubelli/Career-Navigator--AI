package career_Navigator_parent.offer.dto.response;

import career_Navigator_parent.offer.enums.OfferStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOfferResponse {

    private Long id;

    private Long applicationId;

    private Long jobId;

    private String jobTitle;

    private Long studentId;

    private String studentName;

    private Long recruiterId;

    private String recruiterName;

    private String companyName;

    private String offerTitle;

    private BigDecimal annualCtc;

    private BigDecimal baseSalary;

    private BigDecimal bonus;

    private String currency;

    private String employmentType;

    private String workLocation;

    private LocalDate joiningDate;

    private LocalDate offerExpiryDate;

    private Integer probationPeriodMonths;

    private Integer noticePeriodDays;

    private String termsAndConditions;

    private String offerLetterUrl;

    private OfferStatus status;

    private LocalDateTime sentAt;

    private LocalDateTime viewedAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime withdrawnAt;

    private LocalDateTime expiredAt;

    private String studentResponseNotes;

    private String withdrawalReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}