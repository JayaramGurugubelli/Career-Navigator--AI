package career_Navigator_parent.resume.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUploadResponse {

    private Long id;

    private String originalFileName;

    private String storedFileName;

    private String fileType;

    private Long fileSize;

    private Integer version;

    private Boolean active;

    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;

}