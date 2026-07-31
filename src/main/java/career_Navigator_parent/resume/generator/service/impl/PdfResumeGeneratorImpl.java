package career_Navigator_parent.resume.generator.service.impl;


import career_Navigator_parent.resume.generator.service.PdfResumeGenerator;
import career_Navigator_parent.resume.view.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;


@Component
public class PdfResumeGeneratorImpl implements PdfResumeGenerator {


    @Override
    public byte[] generate(
            ResumeView resumeView
    ) {


        try {


            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();


            Document document =
                    new Document(
                            PageSize.A4,
                            40,
                            40,
                            40,
                            40
                    );


            PdfWriter.getInstance(
                    document,
                    outputStream
            );


            document.open();



            Font titleFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD
                    );


            Font headingFont =
                    new Font(
                            Font.HELVETICA,
                            12,
                            Font.BOLD
                    );


            Font normalFont =
                    new Font(
                            Font.HELVETICA,
                            10
                    );



            /*
             * TITLE
             */

            Paragraph title =
                    new Paragraph(
                            resumeView.getResumeTitle(),
                            titleFont
                    );


            title.setAlignment(
                    Element.ALIGN_CENTER
            );


            title.setSpacingAfter(8);


            document.add(title);




            /*
             * PERSONAL INFO
             */


            addHeading(
                    document,
                    "Personal Information",
                    headingFont
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


            if(personal.getPhone()!=null)
                personalInfo.append("\n")
                        .append(personal.getPhone());


            if(personal.getLinkedin()!=null)
                personalInfo.append("\nLinkedIn: ")
                        .append(personal.getLinkedin());


            if(personal.getGithub()!=null)
                personalInfo.append("\nGithub: ")
                        .append(personal.getGithub());


            addParagraph(
                    document,
                    personalInfo.toString(),
                    normalFont
            );




            /*
             * SUMMARY
             */


            if(resumeView.getContent()!=null){


                addHeading(
                        document,
                        "Professional Summary",
                        headingFont
                );


                addParagraph(
                        document,
                        resumeView.getContent()
                                .getProfessionalSummary(),
                        normalFont
                );



                if(resumeView.getContent()
                        .getCareerObjective()!=null){


                    addHeading(
                            document,
                            "Career Objective",
                            headingFont
                    );


                    addParagraph(
                            document,
                            resumeView.getContent()
                                    .getCareerObjective(),
                            normalFont
                    );

                }

            }




            /*
             * EDUCATION
             */


            if(resumeView.getEducation()!=null
                    &&
                    !resumeView.getEducation().isEmpty()){


                addHeading(
                        document,
                        "Education",
                        headingFont
                );


                for(EducationView education:
                        resumeView.getEducation()){


                    addParagraph(
                            document,

                            education.getDegree()
                                    +" - "
                                    +education.getSpecialization()
                                    +"\n"
                                    +education.getInstitutionName()
                                    +" | "
                                    +education.getPassingYear()
                                    +" | "
                                    +education.getPercentage()
                                    +"%",

                            normalFont
                    );

                }

            }





            /*
             * EXPERIENCE
             */


            if(resumeView.getExperience()!=null
                    &&
                    !resumeView.getExperience().isEmpty()){


                addHeading(
                        document,
                        "Experience",
                        headingFont
                );


                for(ExperienceView experience:
                        resumeView.getExperience()){


                    addParagraph(
                            document,

                            experience.getCompanyName()
                                    +" - "
                                    +experience.getJobTitle()
                                    +"\n"
                                    +"Technologies: "
                                    +experience.getTechnologies()
                                    +"\n"
                                    +experience.getDescription(),

                            normalFont
                    );

                }

            }





            /*
             * PROJECTS
             */


            if(resumeView.getProjects()!=null
                    &&
                    !resumeView.getProjects().isEmpty()){


                addHeading(
                        document,
                        "Projects",
                        headingFont
                );


                for(ProjectView project:
                        resumeView.getProjects()){


                    addParagraph(
                            document,

                            project.getTitle()
                                    +"\n"
                                    +"Technologies: "
                                    +project.getTechnologies()
                                    +"\n"
                                    +project.getDescription(),

                            normalFont
                    );

                }

            }






            /*
             * SKILLS
             */


            if(resumeView.getSkills()!=null
                    &&
                    !resumeView.getSkills().isEmpty()){


                addHeading(
                        document,
                        "Technical Skills",
                        headingFont
                );


                String skills =
                        resumeView.getSkills()
                                .stream()
                                .map(
                                        SkillView::getSkillName
                                )
                                .reduce(
                                        "",
                                        (a,b)->a.isEmpty()
                                                ?b
                                                :a+", "+b
                                );


                addParagraph(
                        document,
                        skills,
                        normalFont
                );

            }






            /*
             * CERTIFICATIONS
             */


            if(resumeView.getCertifications()!=null
                    &&
                    !resumeView.getCertifications().isEmpty()){


                addHeading(
                        document,
                        "Certifications",
                        headingFont
                );


                resumeView.getCertifications()
                        .forEach(c -> {

                            try {

                                addParagraph(
                                        document,
                                        c.getCertificationName()
                                                +" - "
                                                +c.getIssuingOrganization(),
                                        normalFont
                                );

                            }
                            catch(Exception e){

                                throw new RuntimeException(e);

                            }

                        });

            }





            /*
             * ACHIEVEMENTS
             */


            if(resumeView.getAchievements()!=null
                    &&
                    !resumeView.getAchievements().isEmpty()){


                addHeading(
                        document,
                        "Achievements",
                        headingFont
                );


                resumeView.getAchievements()
                        .forEach(a -> {

                            try {

                                addParagraph(
                                        document,
                                        a.getTitle()
                                                +"\n"
                                                +a.getDescription(),
                                        normalFont
                                );

                            }
                            catch(Exception e){

                                throw new RuntimeException(e);

                            }

                        });

            }





            /*
             * LANGUAGES
             */


            if(resumeView.getLanguages()!=null
                    &&
                    !resumeView.getLanguages().isEmpty()){


                addHeading(
                        document,
                        "Languages",
                        headingFont
                );


                for(LanguageView language:
                        resumeView.getLanguages()){


                    addParagraph(
                            document,
                            language.getLanguage()
                                    +" - "
                                    +language.getProficiency(),
                            normalFont
                    );

                }

            }





            /*
             * SOCIAL LINKS
             */


            if(resumeView.getSocialLinks()!=null
                    &&
                    !resumeView.getSocialLinks().isEmpty()){


                addHeading(
                        document,
                        "Social Links",
                        headingFont
                );


                for(SocialLinkView link:
                        resumeView.getSocialLinks()){


                    addParagraph(
                            document,
                            link.getPlatform()
                                    +" : "
                                    +link.getUrl(),
                            normalFont
                    );

                }

            }



            document.close();


            return outputStream.toByteArray();


        }
        catch(Exception e){


            throw new RuntimeException(
                    "Failed to generate PDF resume",
                    e
            );

        }

    }





    private void addHeading(
            Document document,
            String text,
            Font font
    )
            throws Exception{


        Paragraph paragraph =
                new Paragraph(
                        text,
                        font
                );


        paragraph.setSpacingBefore(6);

        paragraph.setSpacingAfter(3);


        document.add(paragraph);

    }





    private void addParagraph(
            Document document,
            String text,
            Font font
    )
            throws Exception{


        Paragraph paragraph =
                new Paragraph(
                        text==null?"":text,
                        font
                );


        paragraph.setLeading(13);


        paragraph.setSpacingAfter(4);


        document.add(paragraph);

    }

}