package career_Navigator_parent.ats.engine;


import org.springframework.stereotype.Component;


import java.util.List;


@Component
public class KeywordMatcher {



    public List<String> findMissingSkills(
            List<String> resumeSkills,
            List<String> jobSkills
    ){


        return jobSkills.stream()

                .filter(
                        skill ->
                                !resumeSkills
                                        .contains(skill)
                )

                .toList();

    }



    public List<String> findMatchedSkills(
            List<String> resumeSkills,
            List<String> jobSkills
    ){


        return jobSkills.stream()

                .filter(
                        resumeSkills::contains
                )

                .toList();

    }


}