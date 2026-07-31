package career_Navigator_parent.student.controller;

import career_Navigator_parent.student.dto.request.CreateStudentSocialLinkRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentSocialLinkRequest;
import career_Navigator_parent.student.dto.response.StudentSocialLinkResponse;
import career_Navigator_parent.student.service.StudentSocialLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/social-links")
@RequiredArgsConstructor
public class StudentSocialLinkController {

    private final StudentSocialLinkService socialLinkService;


    @PostMapping
    public ResponseEntity<StudentSocialLinkResponse> createSocialLink(
            @Valid @RequestBody CreateStudentSocialLinkRequest request
    ) {

        return new ResponseEntity<>(
                socialLinkService.createSocialLink(request),
                HttpStatus.CREATED
        );
    }


    @PutMapping("/{socialLinkId}")
    public ResponseEntity<StudentSocialLinkResponse> updateSocialLink(
            @PathVariable Long socialLinkId,
            @Valid @RequestBody UpdateStudentSocialLinkRequest request
    ) {

        return ResponseEntity.ok(
                socialLinkService.updateSocialLink(
                        socialLinkId,
                        request
                )
        );
    }


    @GetMapping("/{socialLinkId}")
    public ResponseEntity<StudentSocialLinkResponse> getSocialLinkById(
            @PathVariable Long socialLinkId
    ) {

        return ResponseEntity.ok(
                socialLinkService.getSocialLinkById(socialLinkId)
        );
    }


    @GetMapping
    public ResponseEntity<List<StudentSocialLinkResponse>> getAllSocialLinks() {

        return ResponseEntity.ok(
                socialLinkService.getAllSocialLinks()
        );
    }


    @DeleteMapping("/{socialLinkId}")
    public ResponseEntity<Void> deleteSocialLink(
            @PathVariable Long socialLinkId
    ) {

        socialLinkService.deleteSocialLink(socialLinkId);

        return ResponseEntity.noContent().build();
    }

}