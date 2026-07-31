package career_Navigator_parent.offer.controller;

import career_Navigator_parent.offer.dto.request.CreateJobOfferRequest;
import career_Navigator_parent.offer.dto.request.UpdateJobOfferRequest;
import career_Navigator_parent.offer.dto.request.WithdrawOfferRequest;
import career_Navigator_parent.offer.dto.response.JobOfferResponse;
import career_Navigator_parent.offer.enums.OfferStatus;
import career_Navigator_parent.offer.service.RecruiterOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterOfferController {

    private final RecruiterOfferService
            recruiterOfferService;

    @PostMapping(
            "/applications/{applicationId}/offers"
    )
    public ResponseEntity<JobOfferResponse>
    createOffer(
            @PathVariable Long applicationId,
            @Valid
            @RequestBody
            CreateJobOfferRequest request
    ) {

        JobOfferResponse response =
                recruiterOfferService.createOffer(
                        applicationId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/offers")
    public ResponseEntity<Page<JobOfferResponse>>
    getMyOffers(
            @RequestParam(required = false)
            OfferStatus status,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterOfferService
                        .getMyOffers(
                                status,
                                pageable
                        )
        );
    }

    @GetMapping("/offers/{offerId}")
    public ResponseEntity<JobOfferResponse>
    getOfferById(
            @PathVariable Long offerId
    ) {

        return ResponseEntity.ok(
                recruiterOfferService
                        .getOfferById(offerId)
        );
    }

    @GetMapping(
            "/applications/{applicationId}/offers"
    )
    public ResponseEntity<Page<JobOfferResponse>>
    getApplicationOffers(
            @PathVariable Long applicationId,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                recruiterOfferService
                        .getApplicationOffers(
                                applicationId,
                                pageable
                        )
        );
    }

    @PutMapping("/offers/{offerId}")
    public ResponseEntity<JobOfferResponse>
    updateOffer(
            @PathVariable Long offerId,

            @Valid
            @RequestBody
            UpdateJobOfferRequest request
    ) {

        return ResponseEntity.ok(
                recruiterOfferService
                        .updateOffer(
                                offerId,
                                request
                        )
        );
    }

    @PatchMapping(
            "/offers/{offerId}/send"
    )
    public ResponseEntity<JobOfferResponse>
    sendOffer(
            @PathVariable Long offerId
    ) {

        return ResponseEntity.ok(
                recruiterOfferService
                        .sendOffer(offerId)
        );
    }

    @PatchMapping(
            "/offers/{offerId}/withdraw"
    )
    public ResponseEntity<JobOfferResponse>
    withdrawOffer(
            @PathVariable Long offerId,

            @Valid
            @RequestBody
            WithdrawOfferRequest request
    ) {

        return ResponseEntity.ok(
                recruiterOfferService
                        .withdrawOffer(
                                offerId,
                                request
                        )
        );
    }

    @DeleteMapping("/offers/{offerId}")
    public ResponseEntity<Void>
    deleteOffer(
            @PathVariable Long offerId
    ) {

        recruiterOfferService
                .deleteOffer(offerId);

        return ResponseEntity.noContent().build();
    }
}