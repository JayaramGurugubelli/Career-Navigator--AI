package careerpilot_parent.ats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsAnalysisResult {

    private Double atsScore;

    @Builder.Default
    private List<String> matchedSkills =
            new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills =
            new ArrayList<>();

    @Builder.Default
    private List<String> extraSkills =
            new ArrayList<>();
}