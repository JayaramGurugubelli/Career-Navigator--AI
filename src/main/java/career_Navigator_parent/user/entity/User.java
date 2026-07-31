package career_Navigator_parent.user.entity;
import career_Navigator_parent.common.constants.ValidationConstants;
import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.shared.enums.AccountStatus;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @NotBlank
    @Size(max = ValidationConstants.FIRST_NAME_MAX)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = ValidationConstants.LAST_NAME_MAX)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Size(max = ValidationConstants.USERNAME_MAX,min =  ValidationConstants.USERNAME_MIN)
    @Column(nullable = false)
    private String username;

    @Email
    @NotBlank
    @Size(max = ValidationConstants.EMAIL_MAX)
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Pattern(regexp = "^[6-9][0-9]{9}$")
    @Size(max = ValidationConstants.PHONE_MAX , min = 10)
    @Column(nullable = false)
    private String phoneNumber;

    @NotBlank
    @Size(min = ValidationConstants.PASSWORD_MIN, max = ValidationConstants.PASSWORD_MAX)
    @Column(nullable = false)
    private String password;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus=AccountStatus.ACTIVE;



    @Builder.Default
    @Column(name = "email_verified",nullable = false)
    private Boolean emailVerified=false;



    @Builder.Default
    @Column(name = "mobile_verified",nullable = false)
    private Boolean mobileVerified=false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled=true;

    //Profile

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(length = ValidationConstants.BIO_MAX)
    private String bio;

    @Column(length = ValidationConstants.HEADLINE_MAX)
    private String headline;
    //Relationships

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<UserRole> roles=new HashSet<>();

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private UserProfile userProfile;
    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private Student student;
}
