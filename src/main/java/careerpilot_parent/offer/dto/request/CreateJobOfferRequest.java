package careerpilot_parent.offer.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobOfferRequest {

    @NotBlank(message = "Offer title is required")
    @Size(max = 200)
    private String offerTitle;

    @NotNull(message = "Annual CTC is required")
    @DecimalMin(
            value = "0.01",
            message = "Annual CTC must be greater than zero"
    )
    private BigDecimal annualCtc;

    @DecimalMin(
            value = "0.00",
            message = "Base salary cannot be negative"
    )
    private BigDecimal baseSalary;

    @DecimalMin(
            value = "0.00",
            message = "Bonus cannot be negative"
    )
    private BigDecimal bonus;

    @NotBlank(message = "Currency is required")
    @Size(max = 10)
    private String currency;

    @NotBlank(message = "Employment type is required")
    @Size(max = 50)
    private String employmentType;

    @NotBlank(message = "Work location is required")
    @Size(max = 200)
    private String workLocation;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Offer expiry date is required")
    private LocalDate offerExpiryDate;

    @Min(value = 0)
    @Max(value = 60)
    private Integer probationPeriodMonths;

    @Min(value = 0)
    @Max(value = 365)
    private Integer noticePeriodDays;

    private String termsAndConditions;

    @Size(max = 1000)
    private String offerLetterUrl;
}