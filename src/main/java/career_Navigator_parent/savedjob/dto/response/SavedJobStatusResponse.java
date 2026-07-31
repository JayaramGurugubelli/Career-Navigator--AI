package career_Navigator_parent.savedjob.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobStatusResponse {

    private Long jobId;

    private boolean saved;

    private Long savedJobId;

    private LocalDateTime savedAt;
}