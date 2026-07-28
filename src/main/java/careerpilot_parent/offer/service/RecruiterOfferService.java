package careerpilot_parent.offer.service;

import careerpilot_parent.offer.dto.request.CreateJobOfferRequest;
import careerpilot_parent.offer.dto.request.UpdateJobOfferRequest;
import careerpilot_parent.offer.dto.request.WithdrawOfferRequest;
import careerpilot_parent.offer.dto.response.JobOfferResponse;
import careerpilot_parent.offer.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruiterOfferService {

    JobOfferResponse createOffer(
            Long applicationId,
            CreateJobOfferRequest request
    );

    Page<JobOfferResponse> getMyOffers(
            OfferStatus status,
            Pageable pageable
    );

    Page<JobOfferResponse> getApplicationOffers(
            Long applicationId,
            Pageable pageable
    );

    JobOfferResponse getOfferById(
            Long offerId
    );

    JobOfferResponse updateOffer(
            Long offerId,
            UpdateJobOfferRequest request
    );

    JobOfferResponse sendOffer(
            Long offerId
    );

    JobOfferResponse withdrawOffer(
            Long offerId,
            WithdrawOfferRequest request
    );

    void deleteOffer(
            Long offerId
    );
}