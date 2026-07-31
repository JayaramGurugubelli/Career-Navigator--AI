package career_Navigator_parent.admin.dto.response;

import career_Navigator_parent.company.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminJobResponse {

    private Long id;

    private String title;

    private String companyName;

    private Long recruiterId;

    private String recruiterName;

    private JobStatus status;

    private String location;

    private LocalDateTime publishedAt;

    private LocalDate applicationDeadline;
}