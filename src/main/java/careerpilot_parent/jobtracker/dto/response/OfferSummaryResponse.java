package careerpilot_parent.jobtracker.dto.response;

import careerpilot_parent.offer.enums.OfferStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferSummaryResponse {

    private Long offerId;
    private Long applicationId;

    private Long jobId;
    private String jobTitle;

    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

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

    private OfferStatus offerStatus;

    private LocalDateTime sentAt;
    private LocalDateTime viewedAt;
    private LocalDateTime respondedAt;

    private String offerLetterUrl;

    private boolean expired;
    private boolean actionRequired;
    private String actionMessage;
}
