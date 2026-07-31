
package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.response.CodingResponses.Detail;
import career_Navigator_parent.coding.dto.response.CodingResponses.Solution;
import career_Navigator_parent.coding.dto.response.CodingResponses.Starter;
import career_Navigator_parent.coding.dto.response.CodingResponses.Summary;
import career_Navigator_parent.coding.enums.ProblemDifficulty;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CodingProblemQueryService {

    Page<Summary> search(
            String keyword,
            ProblemDifficulty difficulty,
            String tag,
            Pageable pageable
    );

    Detail get(String slug);

    Starter starterCode(
            Long problemId,
            ProgrammingLanguage language
    );

    List<Solution> solutions(Long problemId);
}