package career_Navigator_parent.resume.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateResumeUploadRequest {

    @NotNull(message = "Active status is required.")
    private Boolean active;

}