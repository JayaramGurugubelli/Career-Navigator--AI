package career_Navigator_parent.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    private LocalDate dateOfBirth;

    @Size(max = 10)
    private String gender;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;

    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String headline;

    @Size(max = 1000)
    private String bio;
}
