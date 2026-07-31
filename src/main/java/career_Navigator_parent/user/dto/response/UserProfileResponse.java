package career_Navigator_parent.user.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private LocalDate dateOfBirth;

    private String gender;

    private String city;

    private String state;

    private String country;

    private String address;

    private String linkedInUrl;

    private String githubUrl;

    private String portfolioUrl;

    private String resumePath;

    private String leetCodeUrl;

    private String codeforcesUrl;

    private String hackerRankUrl;

    private String codeChefUrl;
}
