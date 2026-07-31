package career_Navigator_parent.resume.controller;


import career_Navigator_parent.resume.dto.request.*;
import career_Navigator_parent.resume.dto.response.ResumeContentResponse;
import career_Navigator_parent.resume.service.ResumeContentService;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/student/resumes/{resumeId}/content")
@RequiredArgsConstructor
public class ResumeContentController {



    private final ResumeContentService contentService;



    @PostMapping
    public ResponseEntity<ResumeContentResponse> createContent(@PathVariable Long resumeId, @Valid @RequestBody CreateResumeContentRequest request){

        return new ResponseEntity<>(
                contentService.createContent(
                        resumeId,
                        request
                ),
                HttpStatus.CREATED
        );

    }



    @GetMapping
    public ResponseEntity<ResumeContentResponse> getContent(@PathVariable Long resumeId){

        return ResponseEntity.ok(
                contentService.getContent(resumeId)
        );

    }



    @PutMapping
    public ResponseEntity<ResumeContentResponse> updateContent(@PathVariable Long resumeId, @Valid @RequestBody UpdateResumeContentRequest request){

        return ResponseEntity.ok(
                contentService.updateContent(
                        resumeId,
                        request
                )
        );

    }



    @DeleteMapping
    public ResponseEntity<Void> deleteContent(@PathVariable Long resumeId){

        contentService.deleteContent(resumeId);

        return ResponseEntity.noContent().build();

    }

}