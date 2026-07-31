package career_Navigator_parent.resume.controller;

import career_Navigator_parent.resume.dto.request.CreateResumeSectionRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeSectionRequest;
import career_Navigator_parent.resume.dto.response.ResumeSectionResponse;
import career_Navigator_parent.resume.service.ResumeSectionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/resumes/{resumeId}/sections")
@RequiredArgsConstructor
public class ResumeSectionController {

    private final ResumeSectionService sectionService;


    @PostMapping
    public ResponseEntity<ResumeSectionResponse> createSection(@PathVariable Long resumeId, @Valid @RequestBody CreateResumeSectionRequest request) {

        ResumeSectionResponse response =
                sectionService.createSection(resumeId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{sectionId}")
    public ResponseEntity<ResumeSectionResponse> getSection(@PathVariable Long resumeId, @PathVariable Long sectionId) {

        return ResponseEntity.ok(sectionService.getSection(resumeId, sectionId));
    }


    @GetMapping
    public ResponseEntity<List<ResumeSectionResponse>> getAllSections(@PathVariable Long resumeId) {

        return ResponseEntity.ok(sectionService.getAllSections(resumeId));
    }


    @PutMapping("/{sectionId}")
    public ResponseEntity<ResumeSectionResponse> updateSection(@PathVariable Long resumeId, @PathVariable Long sectionId, @Valid @RequestBody UpdateResumeSectionRequest request) {

        return ResponseEntity.ok(sectionService.updateSection(resumeId, sectionId, request));
    }


    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long resumeId, @PathVariable Long sectionId) {

        sectionService.deleteSection(resumeId, sectionId);

        return ResponseEntity.noContent().build();
    }

}