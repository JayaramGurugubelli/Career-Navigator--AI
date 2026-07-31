package career_Navigator_parent.resume.view;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectView {
    private String title;

    private String technologies;

    private String description;

    private String githubUrl;

    private String liveUrl;}
