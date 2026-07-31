package career_Navigator_parent.ats.extractor;

import career_Navigator_parent.ats.model.ExtractedSkill;

import java.util.List;

public interface SkillExtractor {

    List<ExtractedSkill> extractSkills(
            String resumeText
    );

}