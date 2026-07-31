package career_Navigator_parent.resume.view;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumeView {


    private String resumeTitle;

    private PersonalInfoView personalInfo;

    private ResumeContentView content;


    private List<EducationView> education;

    private List<ExperienceView> experience;

    private List<ProjectView> projects;

    private List<SkillView> skills;

    private List<CertificationView> certifications;

    private List<AchievementView> achievements;


    private List<LanguageView> languages;

    private List<SocialLinkView> socialLinks;

}