package career_Navigator_parent.company.entity;


import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.user.entity.User;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "recruiter_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_recruiter_profile_user",
                        columnNames = "user_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_recruiter_company",
                        columnList = "company_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfile extends BaseEntity {

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_recruiter_profile_user"
            )
    )
    private User user;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "company_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_recruiter_profile_company"
            )
    )
    private Company company;

    @Column(
            nullable = false,
            length = 120
    )
    private String designation;

    @Column(
            name = "official_email",
            nullable = false,
            length = 150
    )
    private String officialEmail;

    @Column(
            name = "phone_number",
            length = 20
    )
    private String phoneNumber;

    @Column(
            name = "linkedin_url",
            length = 500
    )
    private String linkedinUrl;

    @Column(
            name = "verified",
            nullable = false
    )
    @Builder.Default
    private boolean verified = false;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private boolean active = true;

    @Version
    private Long version;
}