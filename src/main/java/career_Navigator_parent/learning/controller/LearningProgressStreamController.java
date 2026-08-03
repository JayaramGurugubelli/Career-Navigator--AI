package career_Navigator_parent.learning.controller;

import career_Navigator_parent.learning.realtime.LearningProgressPublisher;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/student/learning/stream")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class LearningProgressStreamController {
    private final LearningProgressPublisher publisher;
    private final SecurityUtils security;
    private final StudentRepository students;
    @GetMapping public SseEmitter subscribe(){
        Long userId=security.getCurrentUserId();
        Long studentId=students.findByUserId(userId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Student profile not found.")).getId();
        return publisher.subscribe(studentId);
    }
}
