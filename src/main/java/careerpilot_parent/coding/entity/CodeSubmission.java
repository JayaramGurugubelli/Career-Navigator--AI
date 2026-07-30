package careerpilot_parent.coding.entity;

import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.enums.SubmissionStatus;
import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "coding_submissions",
        indexes = {
                @Index(
                        name = "idx_submission_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_submission_problem",
                        columnList = "problem_id"
                ),
                @Index(
                        name = "idx_submission_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_submission_student_problem",
                        columnList = "student_id, problem_id"
                ),
                @Index(
                        name = "idx_submission_submitted_at",
                        columnList = "submitted_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeSubmission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_submission_student"
            )
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_submission_problem"
            )
    )
    private CodingProblem problem;

    @Enumerated(EnumType.STRING)
    @Column(name = "programming_language", nullable = false, length = 30)
    private ProgrammingLanguage programmingLanguage;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.QUEUED;

    @Column(nullable = false)
    @Builder.Default
    private Integer passedTestCases = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalTestCases = 0;

    @Column
    private Double executionTimeSeconds;

    @Column
    private Long memoryUsedKilobytes;

    @Column(columnDefinition = "LONGTEXT")
    private String compilerOutput;

    @Column(columnDefinition = "LONGTEXT")
    private String runtimeError;

    @Column(columnDefinition = "LONGTEXT")
    private String standardOutput;

    @Column(length = 200)
    private String judgeToken;

    @Column(nullable = false)
    @Builder.Default
    private Boolean plagiarismChecked = false;

    @Column
    private Double plagiarismScore;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column
    private LocalDateTime judgedAt;

    @OneToMany(
            mappedBy = "submission",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<SubmissionTestCaseResult> testCaseResults =
            new ArrayList<>();

    @PrePersist
    protected void initializeSubmission() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }

        if (status == null) {
            status = SubmissionStatus.QUEUED;
        }
    }
    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version;
}