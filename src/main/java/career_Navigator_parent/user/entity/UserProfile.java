package career_Navigator_parent.user.entity;
import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.shared.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_user_profile_user")
    )
    private User user;

    private LocalDateTime dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 50)
    private String country;

    @Size(max = 100)
    private String address;

    @URL
    @Size(max = 255)
    @Column(name = "linkedin_url")
    private String linkedInUrl;

    @URL
    @Column(name = "github_url")
    @Size(max = 255)
    private String githubUrl;

    @URL
    @Size(max = 255)
    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @URL
    @Column(name = "resume_path")
    @Size(max = 255)
    private String resumePath;

    @URL
    @Column(name = "leetcode_url")
    @Size(max = 255)
    private String leetCodeUrl;

    @URL
    @Column(name = "codeforces_url")
    @Size(max = 255)
    private String codeforcesUrl;

    @URL
    @Column(name = "hackerrank_url")
    @Size(max = 255)
    private String hackerRankUrl;

    @URL
    @Column(name = "code_chef_url")
    @Size(max = 255)
    private String codeChefUrl;

}
