package career_Navigator_parent.interviewexperience.dto.response;
import career_Navigator_parent.interview.enums.InterviewType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRoundResponse {

    private Long id;

    private Integer roundNumber;

    private String roundTitle;

    private InterviewType roundType;

    private Integer durationMinutes;

    private Integer displayOrder;

    @Builder.Default
    private List<InterviewQuestionResponse> questions =
            new ArrayList<>();
}