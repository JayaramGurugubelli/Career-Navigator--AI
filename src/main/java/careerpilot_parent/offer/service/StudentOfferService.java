package careerpilot_parent.offer.service;

import careerpilot_parent.offer.dto.request.StudentOfferResponseRequest;
import careerpilot_parent.offer.dto.response.JobOfferResponse;
import careerpilot_parent.offer.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentOfferService {

    Page<JobOfferResponse> getMyOffers(
            OfferStatus status,
            Pageable pageable
    );

    JobOfferResponse getOfferById(
            Long offerId
    );

    JobOfferResponse acceptOffer(
            Long offerId,
            StudentOfferResponseRequest request
    );

    JobOfferResponse rejectOffer(
            Long offerId,
            StudentOfferResponseRequest request
    );
}