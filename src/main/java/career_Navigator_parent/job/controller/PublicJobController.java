package career_Navigator_parent.job.controller;

import career_Navigator_parent.company.dto.response.JobPostingResponse;
import career_Navigator_parent.company.enums.EmploymentType;
import career_Navigator_parent.company.enums.ExperienceLevel;
import career_Navigator_parent.company.enums.WorkMode;
import career_Navigator_parent.job.service.PublicJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class PublicJobController {

    private final PublicJobService publicJobService;

    @GetMapping
    public ResponseEntity<Page<JobPostingResponse>> searchJobs(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            EmploymentType employmentType,

            @RequestParam(required = false)
            WorkMode workMode,

            @RequestParam(required = false)
            ExperienceLevel experienceLevel,

            @PageableDefault(
                    size = 20,
                    sort = "publishedAt",
                    direction = DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                publicJobService.searchJobs(
                        keyword,
                        location,
                        employmentType,
                        workMode,
                        experienceLevel,
                        pageable
                )
        );
    }

    @GetMapping("/{slug}")
    public ResponseEntity<JobPostingResponse> getJobBySlug(
            @PathVariable String slug
    ) {

        return ResponseEntity.ok(
                publicJobService.getJobBySlug(slug)
        );
    }
}