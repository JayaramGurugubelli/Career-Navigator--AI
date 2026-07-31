package career_Navigator_parent.resume.generator.service.impl;


import career_Navigator_parent.resume.generator.service.DocxResumeGenerator;
import career_Navigator_parent.resume.view.*;

import org.apache.poi.xwpf.usermodel.*;

import org.springframework.stereotype.Component;


import java.io.ByteArrayOutputStream;


@Component
public class DocxResumeGeneratorImpl
        implements DocxResumeGenerator {



    @Override
    public byte[] generate(
            ResumeView resumeView
    ) {


        try {


            XWPFDocument document =
                    new XWPFDocument();



            /*
             * TITLE
             */

            XWPFParagraph title =
                    document.createParagraph();


            title.setAlignment(
                    ParagraphAlignment.CENTER
            );


            XWPFRun titleRun =
                    title.createRun();


            titleRun.setText(
                    resumeView.getResumeTitle()
            );


            titleRun.setBold(true);

            titleRun.setFontSize(18);







            /*
             * PERSONAL INFORMATION
             */


            addHeading(
                    document,
                    "Personal Information"
            );


            PersonalInfoView personal =
                    resumeView.getPersonalInfo();



            StringBuilder personalInfo =
                    new StringBuilder();


            personalInfo.append(
                            personal.getFirstName()
                    )
                    .append(" ")
                    .append(
                            personal.getLastName()
                    )
                    .append("\n")
                    .append(
                            personal.getEmail()
                    );


            if(personal.getPhone()!=null){

                personalInfo.append("\n")
                        .append(personal.getPhone());

            }


            if(personal.getLinkedin()!=null){

                personalInfo.append("\nLinkedIn : ")
                        .append(personal.getLinkedin());

            }


            if(personal.getGithub()!=null){

                personalInfo.append("\nGithub : ")
                        .append(personal.getGithub());

            }


            if(personal.getPortfolio()!=null){

                personalInfo.append("\nPortfolio : ")
                        .append(personal.getPortfolio());

            }



            addParagraph(
                    document,
                    personalInfo.toString()
            );










            /*
             * SUMMARY
             */


            if(resumeView.getContent()!=null){


                addHeading(
                        document,
                        "Professional Summary"
                );


                addParagraph(
                        document,
                        resumeView.getContent()
                                .getProfessionalSummary()
                );



                if(resumeView.getContent()
                        .getCareerObjective()!=null){


                    addHeading(
                            document,
                            "Career Objective"
                    );


                    addParagraph(
                            document,
                            resumeView.getContent()
                                    .getCareerObjective()
                    );

                }


            }









            /*
             * EDUCATION
             */


            if(resumeView.getEducation()!=null &&
                    !resumeView.getEducation().isEmpty()){


                addHeading(
                        document,
                        "Education"
                );


                for(EducationView education :
                        resumeView.getEducation()){


                    addParagraph(

                            document,

                            education.getDegree()
                                    +" - "
                                    +education.getInstitutionName()

                                    +"\nSpecialization : "
                                    +education.getSpecialization()

                                    +"\nPassing Year : "
                                    +education.getPassingYear()

                                    +"\nPercentage : "
                                    +education.getPercentage()

                    );

                }

            }









            /*
             * EXPERIENCE
             */


            if(resumeView.getExperience()!=null &&
                    !resumeView.getExperience().isEmpty()){


                addHeading(
                        document,
                        "Experience"
                );


                for(ExperienceView experience :
                        resumeView.getExperience()){


                    addParagraph(

                            document,

                            experience.getCompanyName()
                                    +" - "
                                    +experience.getJobTitle()

                                    +"\nEmployment Type : "
                                    +experience.getEmploymentType()

                                    +"\nTechnologies : "
                                    +experience.getTechnologies()

                                    +"\n"
                                    +experience.getDescription()

                    );


                }

            }









            /*
             * PROJECTS
             */


            if(resumeView.getProjects()!=null &&
                    !resumeView.getProjects().isEmpty()){


                addHeading(
                        document,
                        "Projects"
                );


                for(ProjectView project :
                        resumeView.getProjects()){


                    addParagraph(

                            document,

                            project.getTitle()

                                    +"\nTechnologies : "
                                    +project.getTechnologies()

                                    +"\n"
                                    +project.getDescription()

                                    +"\nGithub : "
                                    +(project.getGithubUrl()==null
                                    ? ""
                                    :project.getGithubUrl())

                    );


                }

            }










            /*
             * SKILLS
             */


            if(resumeView.getSkills()!=null &&
                    !resumeView.getSkills().isEmpty()){


                addHeading(
                        document,
                        "Skills"
                );


                for(SkillView skill :
                        resumeView.getSkills()){


                    addParagraph(

                            document,

                            skill.getSkillName()
                                    +" - "
                                    +skill.getProficiency()

                    );


                }


            }











            /*
             * CERTIFICATIONS
             */


            if(resumeView.getCertifications()!=null &&
                    !resumeView.getCertifications().isEmpty()){


                addHeading(
                        document,
                        "Certifications"
                );


                for(CertificationView certification :
                        resumeView.getCertifications()){


                    addParagraph(

                            document,

                            certification.getCertificationName()
                                    +" - "
                                    +certification.getIssuingOrganization()

                    );


                }


            }









            /*
             * ACHIEVEMENTS
             */


            if(resumeView.getAchievements()!=null &&
                    !resumeView.getAchievements().isEmpty()){


                addHeading(
                        document,
                        "Achievements"
                );


                for(AchievementView achievement :
                        resumeView.getAchievements()){


                    addParagraph(

                            document,

                            achievement.getTitle()
                                    +"\n"
                                    +achievement.getDescription()

                    );


                }


            }










            /*
             * LANGUAGES
             */


            if(resumeView.getLanguages()!=null &&
                    !resumeView.getLanguages().isEmpty()){


                addHeading(
                        document,
                        "Languages"
                );


                for(LanguageView language :
                        resumeView.getLanguages()){


                    addParagraph(

                            document,

                            language.getLanguage()
                                    +" - "
                                    +language.getProficiency()

                    );


                }


            }









            /*
             * SOCIAL LINKS
             */


            if(resumeView.getSocialLinks()!=null &&
                    !resumeView.getSocialLinks().isEmpty()){


                addHeading(
                        document,
                        "Social Links"
                );


                for(SocialLinkView link :
                        resumeView.getSocialLinks()){


                    addParagraph(

                            document,

                            link.getPlatform()
                                    +" : "
                                    +link.getUrl()

                    );


                }


            }







            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();



            document.write(output);


            document.close();



            return output.toByteArray();


        }
        catch(Exception e){


            throw new RuntimeException(
                    "Failed to generate DOCX resume",
                    e
            );

        }

    }







    private void addHeading(
            XWPFDocument document,
            String text
    ){


        XWPFParagraph paragraph =
                document.createParagraph();


        XWPFRun run =
                paragraph.createRun();


        run.setText(text);

        run.setBold(true);

        run.setFontSize(14);

    }








    private void addParagraph(
            XWPFDocument document,
            String text
    ){


        XWPFParagraph paragraph =
                document.createParagraph();


        XWPFRun run =
                paragraph.createRun();


        run.setText(
                text == null ? "" : text
        );


        run.setFontSize(11);

    }


}