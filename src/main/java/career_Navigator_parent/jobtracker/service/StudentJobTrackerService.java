package career_Navigator_parent.jobtracker.service;

import career_Navigator_parent.job.dto.response.ApplicationStatusHistoryResponse;
import career_Navigator_parent.jobtracker.dto.response.*;
import career_Navigator_parent.offer.enums.OfferStatus;
import career_Navigator_parent.shared.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StudentJobTrackerService {

    JobTrackerDashboardResponse getDashboard(
            LocalDate fromDate,
            LocalDate toDate
    );

    Page<JobTrackerItemResponse> getApplications(
            ApplicationStatus status,
            String keyword,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );

    JobTrackerItemResponse getApplication(Long applicationId);

    List<ApplicationStatusHistoryResponse> getApplicationHistory(
            Long applicationId
    );

    List<ApplicationTimelineResponse> getApplicationTimeline(
            Long applicationId
    );

    List<UpcomingInterviewResponse> getUpcomingInterviews();

    List<OfferSummaryResponse> getOffers(OfferStatus status);
}
