package career_Navigator_parent.student.mapper;

import career_Navigator_parent.student.dto.request.CreateStudentLanguageRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentLanguageRequest;
import career_Navigator_parent.student.dto.response.StudentLanguageResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentLanguage;
import org.springframework.stereotype.Component;

@Component
public class StudentLanguageMapper {

    /**
     * Create Entity
     */
    public StudentLanguage toEntity(
            CreateStudentLanguageRequest request,
            Student student
    ) {

        return StudentLanguage.builder()
                .student(student)
                .languageName(request.getLanguageName())
                .proficiency(request.getProficiency())
                .build();
    }


    /**
     * Entity → Response
     */
    public StudentLanguageResponse toResponse(
            StudentLanguage language
    ) {

        return StudentLanguageResponse.builder()
                .id(language.getId())
                .languageName(language.getLanguageName())
                .proficiency(language.getProficiency())
                .build();
    }


    /**
     * Update Existing Entity
     */
    public void updateEntity(
            StudentLanguage language,
            UpdateStudentLanguageRequest request
    ) {

        language.setLanguageName(
                request.getLanguageName()
        );

        language.setProficiency(
                request.getProficiency()
        );
    }

}