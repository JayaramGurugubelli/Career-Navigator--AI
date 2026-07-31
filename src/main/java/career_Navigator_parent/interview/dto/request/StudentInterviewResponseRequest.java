package career_Navigator_parent.interview.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInterviewResponseRequest {

    @Size(
            max = 2000,
            message = "Response notes must not exceed 2000 characters"
    )
    private String notes;
}