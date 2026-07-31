package career_Navigator_parent.ats.extractor;

import career_Navigator_parent.resume.ats.dictionary.SkillDictionary;
import career_Navigator_parent.ats.model.ExtractedSkill;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillExtractorImpl implements SkillExtractor {

    private final SkillDictionary dictionary;

    @Override
    public List<ExtractedSkill> extractSkills(
            String resumeText
    ) {

        List<ExtractedSkill> extractedSkills =
                new ArrayList<>();

        if (resumeText == null || resumeText.isBlank()) {
            return extractedSkills;
        }

        String lowerText =
                resumeText.toLowerCase();

        for (String skill : dictionary.getSkills()) {

            int count = countOccurrences(
                    lowerText,
                    skill.toLowerCase()
            );

            if (count > 0) {

                extractedSkills.add(

                        ExtractedSkill.builder()

                                .skill(skill)

                                .frequency(count)

                                .build()

                );

            }

        }

        return extractedSkills;

    }

    private int countOccurrences(
            String text,
            String keyword
    ) {

        int count = 0;

        int index = 0;

        while ((index = text.indexOf(keyword, index)) != -1) {

            count++;

            index += keyword.length();

        }

        return count;

    }

}