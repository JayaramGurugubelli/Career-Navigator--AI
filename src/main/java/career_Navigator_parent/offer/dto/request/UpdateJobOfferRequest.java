package career_Navigator_parent.offer.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateJobOfferRequest {

    @NotBlank(message = "Offer title is required")
    @Size(max = 200)
    private String offerTitle;

    @NotNull(message = "Annual CTC is required")
    @DecimalMin(
            value = "0.01",
            message = "Annual CTC must be greater than zero"
    )
    private BigDecimal annualCtc;

    @DecimalMin(value = "0.00")
    private BigDecimal baseSalary;

    @DecimalMin(value = "0.00")
    private BigDecimal bonus;

    @NotBlank
    @Size(max = 10)
    private String currency;

    @NotBlank
    @Size(max = 50)
    private String employmentType;

    @NotBlank
    @Size(max = 200)
    private String workLocation;

    @NotNull
    private LocalDate joiningDate;

    @NotNull
    private LocalDate offerExpiryDate;

    @Min(0)
    @Max(60)
    private Integer probationPeriodMonths;

    @Min(0)
    @Max(365)
    private Integer noticePeriodDays;

    private String termsAndConditions;

    @Size(max = 1000)
    private String offerLetterUrl;
}