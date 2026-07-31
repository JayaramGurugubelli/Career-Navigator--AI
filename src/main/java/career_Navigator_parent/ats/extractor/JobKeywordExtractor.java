package career_Navigator_parent.ats.extractor;

import java.util.List;

public interface JobKeywordExtractor {

    List<JobKeyword> extractKeywords(
            String jobDescription
    );

}
