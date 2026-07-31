package career_Navigator_parent.resume.assembler;


import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.resume.repository.ResumeContentRepository;
import career_Navigator_parent.resume.repository.ResumeRepository;
import career_Navigator_parent.resume.view.*;

import career_Navigator_parent.security.util.SecurityUtils;

import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeAssemblerServiceImpl implements ResumeAssemblerService {


    private final ResumeRepository resumeRepository;

    private final StudentRepository studentRepository;

    private final StudentEducationRepository educationRepository;

    private final StudentExperienceRepository experienceRepository;

    private final StudentProjectRepository projectRepository;

    private final StudentSkillRepository skillRepository;

    private final StudentCertificationRepository certificationRepository;

    private final StudentAchievementRepository achievementRepository;

    private final StudentLanguageRepository languageRepository;

    private final StudentSocialLinkRepository socialLinkRepository;

    private final ResumeContentRepository resumeContentRepository;

    private final SecurityUtils securityUtils;



    @Override
    public ResumeView assembleResume(Long resumeId) {


        Long userId =
                securityUtils.getCurrentUserId();


        Student student =
                studentRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student not found"
                                )
                        );


        Resume resume =
                resumeRepository
                        .findById(resumeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume not found : " + resumeId
                                )
                        );


        if(!resume.getStudent()
                .getId()
                .equals(student.getId())) {


            throw new ResourceNotFoundException(
                    "Resume does not belong to student"
            );
        }



        return ResumeView.builder()

                .resumeTitle(
                        resume.getResumeTitle()
                )

                .personalInfo(
                        buildPersonalInfo(student)
                )

                .content(
                        buildResumeContent(resumeId)
                )

                .education(
                        buildEducation(student.getId())
                )

                .experience(
                        buildExperience(student.getId())
                )

                .projects(
                        buildProjects(student.getId())
                )

                .skills(
                        buildSkills(student.getId())
                )

                .certifications(
                        buildCertifications(student.getId())
                )

                .achievements(
                        buildAchievements(student.getId())
                )

                .languages(
                        buildLanguages(student.getId())
                )

                .socialLinks(
                        buildSocialLinks(student.getId())
                )

                .build();

    }



    private PersonalInfoView buildPersonalInfo(Student student){

        return PersonalInfoView.builder()

                .firstName(
                        student.getUser().getFirstName()
                )

                .lastName(
                        student.getUser().getLastName()
                )

                .email(
                        student.getUser().getEmail()
                )

                .build();

    }



    private List<EducationView> buildEducation(Long studentId){

        return educationRepository
                .findByStudentId(studentId)
                .stream()
                .map(e ->

                        EducationView.builder()

                                .degree(
                                        e.getDegree()
                                )

                                .specialization(
                                        e.getSpecialization()
                                )

                                .institutionName(
                                        e.getInstitutionName()
                                )

                                .passingYear(
                                        e.getPassingYear()
                                )

                                .percentage(
                                        e.getPercentage()
                                )

                                .build()

                )
                .toList();

    }



    private List<ExperienceView> buildExperience(Long studentId){


        return experienceRepository
                .findByStudentId(studentId)
                .stream()
                .map(e ->

                        ExperienceView.builder()

                                .companyName(
                                        e.getCompanyName()
                                )

                                .jobTitle(
                                        e.getJobTitle()
                                )

                                .employmentType(
                                        String.valueOf(
                                                e.getEmploymentType()
                                        )
                                )

                                .startDate(
                                        e.getStartDate()
                                )

                                .endDate(
                                        e.getEndDate()
                                )

                                .currentlyWorking(
                                        e.getCurrentlyWorking()
                                )

                                .technologies(
                                        e.getTechnologies()
                                )

                                .description(
                                        e.getDescription()
                                )

                                .build()

                )
                .toList();

    }



    private List<ProjectView> buildProjects(Long studentId){


        return projectRepository
                .findByStudentIdOrderByStartDateDesc(studentId)
                .stream()
                .map(p ->


                        ProjectView.builder()

                                .title(
                                        p.getProjectTitle()
                                )

                                .technologies(
                                        p.getTechnologiesUsed()
                                )

                                .description(
                                        p.getDescription()
                                )

                                .githubUrl(
                                        p.getGithubUrl()
                                )

                                .liveUrl(
                                        p.getLiveDemoUrl()
                                )

                                .build()

                )
                .toList();

    }



    private List<SkillView> buildSkills(Long studentId){


        return skillRepository
                .findByStudentId(studentId)
                .stream()
                .map(skill ->


                        SkillView.builder()

                                .skillName(
                                        skill.getSkillName()
                                )

                                .proficiency(
                                        String.valueOf(skill.getProficiencyLevel()))

                                .build()

                )
                .toList();

    }



    private List<CertificationView> buildCertifications(Long studentId){


        return certificationRepository
                .findByStudentId(studentId)
                .stream()
                .map(c ->


                        CertificationView.builder()

                                .certificationName(
                                        c.getCertificationName()
                                )

                                .issuingOrganization(
                                        c.getIssuingOrganization()
                                )

                                .issueDate(
                                        c.getIssueDate()
                                )

                                .credentialId(
                                        c.getCredentialId()
                                )

                                .build()

                )
                .toList();

    }



    private List<AchievementView> buildAchievements(Long studentId){


        return achievementRepository
                .findByStudentId(studentId)
                .stream()
                .map(a ->


                        AchievementView.builder()

                                .title(
                                        a.getTitle()
                                )

                                .description(
                                        a.getDescription()
                                )

                                .build()

                )
                .toList();

    }



    private List<LanguageView> buildLanguages(Long studentId){


        return languageRepository
                .findByStudentId(studentId)
                .stream()
                .map(language ->


                        LanguageView.builder()

                                .language(
                                        language.getLanguageName()
                                )

                                .proficiency(
                                        String.valueOf(
                                                language.getProficiency()
                                        )
                                )

                                .build()

                )
                .toList();

    }



    private List<SocialLinkView> buildSocialLinks(Long studentId){


        return socialLinkRepository
                .findByStudentId(studentId)
                .stream()
                .map(link ->


                        SocialLinkView.builder()

                                .platform(
                                        String.valueOf(
                                                link.getPlatform()
                                        )
                                )

                                .url(
                                        link.getUrl()
                                )

                                .build()

                )
                .toList();

    }



    private ResumeContentView buildResumeContent(Long resumeId){


        return resumeContentRepository
                .findByResumeId(resumeId)
                .map(content ->


                        ResumeContentView.builder()

                                .professionalSummary(
                                        content.getProfessionalSummary()
                                )

                                .careerObjective(
                                        content.getCareerObjective()
                                )

                                .build()

                )
                .orElse(null);

    }

}