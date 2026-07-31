package career_Navigator_parent.offer.controller;

import career_Navigator_parent.offer.dto.request.StudentOfferResponseRequest;
import career_Navigator_parent.offer.dto.response.JobOfferResponse;
import career_Navigator_parent.offer.enums.OfferStatus;
import career_Navigator_parent.offer.service.StudentOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/offers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentOfferController {

    private final StudentOfferService
            studentOfferService;

    @GetMapping
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
                studentOfferService
                        .getMyOffers(
                                status,
                                pageable
                        )
        );
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<JobOfferResponse>
    getOfferById(
            @PathVariable Long offerId
    ) {

        return ResponseEntity.ok(
                studentOfferService
                        .getOfferById(offerId)
        );
    }

    @PatchMapping("/{offerId}/accept")
    public ResponseEntity<JobOfferResponse>
    acceptOffer(
            @PathVariable Long offerId,

            @Valid
            @RequestBody
            StudentOfferResponseRequest request
    ) {

        return ResponseEntity.ok(
                studentOfferService
                        .acceptOffer(
                                offerId,
                                request
                        )
        );
    }

    @PatchMapping("/{offerId}/reject")
    public ResponseEntity<JobOfferResponse>
    rejectOffer(
            @PathVariable Long offerId,

            @Valid
            @RequestBody
            StudentOfferResponseRequest request
    ) {

        return ResponseEntity.ok(
                studentOfferService
                        .rejectOffer(
                                offerId,
                                request
                        )
        );
    }
}