package career_Navigator_parent.interviewexperience.dto.response;

import career_Navigator_parent.interviewexperience.enums.InterviewQuestionCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestionResponse {

    private Long id;

    private String question;

    private InterviewQuestionCategory category;

    private String topic;

    private String additionalDetails;

    private Integer displayOrder;
}