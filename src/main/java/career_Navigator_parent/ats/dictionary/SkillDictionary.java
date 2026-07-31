package career_Navigator_parent.resume.ats.dictionary;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SkillDictionary {

    private static final Set<String> SKILLS = Set.of(

            "java",
            "spring",
            "spring boot",
            "hibernate",
            "jpa",
            "mysql",
            "postgresql",
            "oracle",
            "mongodb",
            "redis",

            "docker",
            "kubernetes",
            "aws",
            "azure",
            "gcp",

            "git",
            "github",
            "maven",
            "gradle",

            "rest",
            "rest api",
            "microservices",

            "junit",
            "mockito",

            "angular",
            "react",
            "javascript",
            "typescript",

            "html",
            "css",

            "linux",

            "jenkins",

            "terraform",

            "kafka",

            "rabbitmq"

    );

    public Set<String> getSkills() {
        return SKILLS;
    }

}