package career_Navigator_parent.student.controller;

import career_Navigator_parent.student.dto.request.CreateStudentLanguageRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentLanguageRequest;
import career_Navigator_parent.student.dto.response.StudentLanguageResponse;
import career_Navigator_parent.student.service.StudentLanguageService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/languages")
@RequiredArgsConstructor
public class StudentLanguageController {

    private final StudentLanguageService languageService;

    @PostMapping
    public ResponseEntity<StudentLanguageResponse> createLanguage(
            @Valid @RequestBody CreateStudentLanguageRequest request
    ) {

        return new ResponseEntity<>(
                languageService.createLanguage(request),
                HttpStatus.CREATED
        );
    }


    @PutMapping("/{languageId}")
    public ResponseEntity<StudentLanguageResponse> updateLanguage(
            @PathVariable Long languageId,
            @Valid @RequestBody UpdateStudentLanguageRequest request
    ) {

        return ResponseEntity.ok(
                languageService.updateLanguage(
                        languageId,
                        request
                )
        );
    }


    @GetMapping("/{languageId}")
    public ResponseEntity<StudentLanguageResponse> getLanguageById(
            @PathVariable Long languageId
    ) {

        return ResponseEntity.ok(
                languageService.getLanguageById(languageId)
        );
    }


    @GetMapping
    public ResponseEntity<List<StudentLanguageResponse>> getAllLanguages() {

        return ResponseEntity.ok(
                languageService.getAllLanguages()
        );
    }


    @DeleteMapping("/{languageId}")
    public ResponseEntity<Void> deleteLanguage(
            @PathVariable Long languageId
    ) {

        languageService.deleteLanguage(languageId);

        return ResponseEntity.noContent().build();
    }

}