package career_Navigator_parent.offer.service;

import career_Navigator_parent.offer.dto.request.CreateJobOfferRequest;
import career_Navigator_parent.offer.dto.request.UpdateJobOfferRequest;
import career_Navigator_parent.offer.dto.request.WithdrawOfferRequest;
import career_Navigator_parent.offer.dto.response.JobOfferResponse;
import career_Navigator_parent.offer.enums.OfferStatus;
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