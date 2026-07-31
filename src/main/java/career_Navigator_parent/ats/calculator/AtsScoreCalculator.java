package career_Navigator_parent.ats.calculator;

import career_Navigator_parent.ats.model.AtsAnalysisResult;
import career_Navigator_parent.ats.model.ExtractedSkill;
import career_Navigator_parent.ats.extractor.JobKeyword;

import java.util.List;

public interface AtsScoreCalculator {

    AtsAnalysisResult calculateScore(

            List<ExtractedSkill> resumeSkills,

            List<JobKeyword> jobKeywords

    );

}