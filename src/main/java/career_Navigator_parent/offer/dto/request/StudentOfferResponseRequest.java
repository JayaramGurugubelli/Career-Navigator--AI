package career_Navigator_parent.offer.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentOfferResponseRequest {

    @Size(
            max = 2000,
            message = "Response notes cannot exceed 2000 characters"
    )
    private String notes;
}