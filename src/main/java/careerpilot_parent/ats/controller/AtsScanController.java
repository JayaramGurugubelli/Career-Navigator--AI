package careerpilot_parent.ats.controller;

import careerpilot_parent.ats.dto.request.CreateAtsScanRequest;
import careerpilot_parent.ats.dto.response.AtsScanResponse;
import careerpilot_parent.ats.service.AtsScanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ats/scans")
@RequiredArgsConstructor
public class AtsScanController {

    private final AtsScanService atsScanService;

    /**
     * Create a new ATS scan.
     */
    @PostMapping
    public ResponseEntity<AtsScanResponse> createScan(
            @Valid @RequestBody CreateAtsScanRequest request
    ) {

        AtsScanResponse response =
                atsScanService.createScan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get all ATS scans belonging to the logged-in student.
     */
    @GetMapping
    public ResponseEntity<List<AtsScanResponse>> getMyScans() {

        return ResponseEntity.ok(
                atsScanService.getMyScans()
        );
    }

    /**
     * Get one ATS scan.
     */
    @GetMapping("/{scanId}")
    public ResponseEntity<AtsScanResponse> getScanById(
            @PathVariable Long scanId
    ) {

        return ResponseEntity.ok(
                atsScanService.getScanById(scanId)
        );
    }

    /**
     * Delete an ATS scan.
     */
    @DeleteMapping("/{scanId}")
    public ResponseEntity<Void> deleteScan(
            @PathVariable Long scanId
    ) {

        atsScanService.deleteScan(scanId);

        return ResponseEntity
                .noContent()
                .build();
    }
}