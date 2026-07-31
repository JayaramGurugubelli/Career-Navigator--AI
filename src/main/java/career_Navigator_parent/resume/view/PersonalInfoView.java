package career_Navigator_parent.resume.view;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfoView {
    private String firstName;
    private String lastName;

    private String email;

    private String phone;

    private String address;

    private String linkedin;

    private String github;

    private String portfolio;
}
