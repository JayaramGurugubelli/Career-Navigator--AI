package careerpilot_parent.interviewexperience.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.interview.enums.InterviewType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "interview_experience_rounds",
        indexes = {
                @Index(
                        name = "idx_interview_round_experience",
                        columnList = "interview_experience_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceRound extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "interview_experience_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_round_interview_experience"
            )
    )
    private InterviewExperience interviewExperience;

    @Column(nullable = false)
    private Integer roundNumber;

    @Column(
            nullable = false,
            length = 150
    )
    private String roundTitle;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private InterviewType roundType;

    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer displayOrder;

    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "interviewRound",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<InterviewQuestion> questions =
            new ArrayList<>();

    public void addQuestion(
            InterviewQuestion question
    ) {

        questions.add(question);
        question.setInterviewRound(this);
    }

    public void removeQuestion(
            InterviewQuestion question
    ) {

        questions.remove(question);
        question.setInterviewRound(null);
    }
}