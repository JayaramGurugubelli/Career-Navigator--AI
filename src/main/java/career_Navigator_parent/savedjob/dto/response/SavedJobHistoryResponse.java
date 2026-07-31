package career_Navigator_parent.savedjob.dto.response;

import career_Navigator_parent.savedjob.enums.SavedJobAction;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobHistoryResponse {

    private Long historyId;

    private Long jobId;

    private String jobTitle;

    private String companyName;

    private String location;

    private SavedJobAction action;

    private LocalDateTime actionAt;
}