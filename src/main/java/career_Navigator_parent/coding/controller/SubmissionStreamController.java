package career_Navigator_parent.coding.controller;

import career_Navigator_parent.coding.realtime.SseSubmissionEventPublisher;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/student/coding/submissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class SubmissionStreamController {

    private final SseSubmissionEventPublisher submissionEventPublisher;

    private final SecurityUtils securityUtils;

    private final StudentRepository studentRepository;

    @GetMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamSubmissionUpdates() {

        Student student =
                studentRepository
                        .findByUserId(
                                securityUtils.getCurrentUserId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Student profile not found."
                                )
                        );

        return submissionEventPublisher.subscribe(
                student.getId()
        );
    }
}