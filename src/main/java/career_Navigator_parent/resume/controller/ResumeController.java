package career_Navigator_parent.resume.controller;

import career_Navigator_parent.resume.dto.request.CreateResumeRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeRequest;
import career_Navigator_parent.resume.dto.response.ResumeResponse;
import career_Navigator_parent.resume.service.ResumeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/student/resumes")
@RequiredArgsConstructor
public class ResumeController {


    private final ResumeService resumeService;



    @PostMapping
    public ResponseEntity<ResumeResponse> createResume(@Valid @RequestBody CreateResumeRequest request){

        return new ResponseEntity<>(resumeService.createResume(request), HttpStatus.CREATED);

    }



    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getAllResumes(){

        return ResponseEntity.ok(resumeService.getAllResumes());
    }



    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getResumeById(@PathVariable Long resumeId){

        return ResponseEntity.ok(resumeService.getResumeById(resumeId));
    }



    @PutMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> updateResume(@PathVariable Long resumeId, @Valid @RequestBody UpdateResumeRequest request){

        return ResponseEntity.ok(resumeService.updateResume(resumeId, request));
    }



    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long resumeId){

        resumeService.deleteResume(resumeId);

        return ResponseEntity.noContent().build();
    }

}
