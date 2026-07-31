package career_Navigator_parent.student.service;

import career_Navigator_parent.student.dto.request.CreateStudentLanguageRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentLanguageRequest;
import career_Navigator_parent.student.dto.response.StudentLanguageResponse;

import java.util.List;

public interface StudentLanguageService {

    StudentLanguageResponse createLanguage(
            CreateStudentLanguageRequest request
    );


    StudentLanguageResponse updateLanguage(
            Long languageId,
            UpdateStudentLanguageRequest request
    );


    StudentLanguageResponse getLanguageById(
            Long languageId
    );


    List<StudentLanguageResponse> getAllLanguages();


    void deleteLanguage(
            Long languageId
    );

}