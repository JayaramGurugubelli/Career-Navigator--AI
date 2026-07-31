package career_Navigator_parent.ats.calculator;

import career_Navigator_parent.ats.extractor.JobKeyword;
import career_Navigator_parent.ats.model.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AtsScoreCalculatorImpl implements AtsScoreCalculator {

    @Override
    public AtsAnalysisResult calculateScore(

            List<ExtractedSkill> resumeSkills,

            List<JobKeyword> jobKeywords
    ) {

        Set<String> resumeSet =

                resumeSkills.stream()

                        .map(skill ->
                                skill.getSkill().toLowerCase())

                        .collect(Collectors.toSet());



        Set<String> jobSet =

                jobKeywords.stream()

                        .map(job ->
                                job.getKeyword().toLowerCase())

                        .collect(Collectors.toSet());



        List<String> matched =

                new ArrayList<>();


        List<String> missing =

                new ArrayList<>();


        List<String> extra =

                new ArrayList<>();



        for(String skill : jobSet){

            if(resumeSet.contains(skill)){

                matched.add(skill);

            }
            else{

                missing.add(skill);

            }

        }



        for(String skill : resumeSet){

            if(!jobSet.contains(skill)){

                extra.add(skill);

            }

        }



        double score = 0;

        if(!jobSet.isEmpty()){

            score =

                    ((double) matched.size()

                            / jobSet.size())

                            * 100.0;

        }



        return AtsAnalysisResult.builder()

                .atsScore(
                        Math.round(score * 100.0) / 100.0
                )

                .matchedSkills(matched)

                .missingSkills(missing)

                .extraSkills(extra)

                .build();

    }

}