package career_Navigator_parent.company.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.company.enums.CompanySize;
import career_Navigator_parent.company.enums.CompanyStatus;
import career_Navigator_parent.user.entity.User;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_company_slug",
                        columnNames = "slug"
                ),
                @UniqueConstraint(
                        name = "uk_company_owner",
                        columnNames = "owner_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_company_name",
                        columnList = "name"
                ),
                @Index(
                        name = "idx_company_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_company_verified",
                        columnList = "verified"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @Version
    private Long version;

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            nullable = false,
            length = 180
    )
    private String slug;

    @Column(
            length = 5000
    )
    private String description;

    @Column(
            length = 120
    )
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "company_size",
            length = 30
    )
    private CompanySize companySize;

    @Column(
            name = "website_url",
            length = 500
    )
    private String websiteUrl;

    @Column(
            name = "logo_url",
            length = 1000
    )
    private String logoUrl;

    @Column(
            length = 255
    )
    private String headquarters;

    @Column(
            name = "founded_year"
    )
    private Integer foundedYear;

    @Column(
            name = "contact_email",
            length = 150
    )
    private String contactEmail;

    @Column(
            name = "contact_phone",
            length = 20
    )
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private CompanyStatus status =
            CompanyStatus.ACTIVE;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean verified = false;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_company_owner"
            )
    )
    private User owner;

    /*
     * Compatibility/business helper.
     */
    @Transient
    public boolean isActive() {

        return CompanyStatus.ACTIVE.equals(status);
    }

    /*
     * Boolean fields using wrapper types normally generate
     * getVerified(), not isVerified().
     */
    @Transient
    public boolean isVerified() {

        return Boolean.TRUE.equals(verified);
    }

    public void activate() {

        this.status = CompanyStatus.ACTIVE;
    }

    public void deactivate() {

        this.status = CompanyStatus.INACTIVE;
    }

    public void suspend() {

        this.status = CompanyStatus.SUSPENDED;
    }
}