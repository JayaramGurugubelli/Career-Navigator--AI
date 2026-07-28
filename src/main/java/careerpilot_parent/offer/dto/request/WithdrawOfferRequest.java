package careerpilot_parent.offer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawOfferRequest {

    @NotBlank(message = "Withdrawal reason is required")
    @Size(max = 2000)
    private String reason;
}