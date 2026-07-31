package career_Navigator_parent.job.dto.request;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawJobApplicationRequest {

    @Size(
            max = 1000,
            message = "Withdrawal reason must not exceed 1000 characters"
    )
    private String reason;
}