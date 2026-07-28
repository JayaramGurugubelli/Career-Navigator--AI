package careerpilot_parent.interviewexperience.service;

import careerpilot_parent.interviewexperience.dto.request.CreateInterviewExperienceReportRequest;
import careerpilot_parent.interviewexperience.dto.request.ReviewInterviewExperienceReportRequest;
import careerpilot_parent.interviewexperience.dto.response.InterviewExperienceReportResponse;
import careerpilot_parent.interviewexperience.dto.response.PageResponse;
import careerpilot_parent.interviewexperience.enums.InterviewExperienceReportReason;
import org.springframework.data.domain.Pageable;

public interface InterviewExperienceReportService {

    /*
     * Current user reports an approved experience.
     * One report per user per experience.
     */
    InterviewExperienceReportResponse reportExperience(
            Long experienceId,
            CreateInterviewExperienceReportRequest request
    );

    /*
     * Admin list of reports.
     */
    PageResponse<InterviewExperienceReportResponse> getReports(
            Boolean reviewed,
            Boolean resolved,
            InterviewExperienceReportReason reason,
            Pageable pageable
    );

    /*
     * Admin gets one report.
     */
    InterviewExperienceReportResponse getReportById(
            Long reportId
    );

    /*
     * Admin gets all reports belonging to one experience.
     */
    PageResponse<InterviewExperienceReportResponse>
    getReportsForExperience(
            Long experienceId,
            Pageable pageable
    );

    /*
     * Admin resolves or reviews a report.
     */
    InterviewExperienceReportResponse reviewReport(
            Long reportId,
            ReviewInterviewExperienceReportRequest request
    );

    /*
     * Number of unresolved reports for admin dashboard.
     */
    long getPendingReportCount();

    /*
     * Recalculates the report counter if a transaction previously failed.
     */
    long recalculateExperienceReportCount(
            Long experienceId
    );
}