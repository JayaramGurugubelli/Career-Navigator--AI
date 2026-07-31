package career_Navigator_parent.ats.extractor;

import career_Navigator_parent.resume.ats.dictionary.SkillDictionary;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobKeywordExtractorImpl
        implements JobKeywordExtractor {

    private final SkillDictionary skillDictionary;

    @Override
    public List<JobKeyword> extractKeywords(
            String jobDescription
    ) {

        List<JobKeyword> keywords =
                new ArrayList<>();

        if (jobDescription == null ||
                jobDescription.isBlank()) {

            return keywords;

        }

        String lower =
                jobDescription.toLowerCase();

        for (String skill :
                skillDictionary.getSkills()) {

            int count =
                    countOccurrences(
                            lower,
                            skill.toLowerCase()
                    );

            if (count > 0) {

                keywords.add(

                        JobKeyword.builder()
                                .keyword(skill)
                                .frequency(count)
                                .build()

                );

            }

        }

        return keywords;

    }

    private int countOccurrences(
            String text,
            String keyword
    ) {

        int count = 0;

        int index = 0;

        while ((index =
                text.indexOf(keyword, index)) != -1) {

            count++;

            index += keyword.length();

        }

        return count;

    }

}