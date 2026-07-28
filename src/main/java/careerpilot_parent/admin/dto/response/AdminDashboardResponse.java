package careerpilot_parent.admin.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalStudents;
    private long totalRecruiters;
    private long totalAdmins;

    private long totalCompanies;
    private long approvedCompanies;
    private long pendingCompanies;

    private long verifiedRecruiters;
    private long pendingRecruiters;
    private long activeRecruiters;

    private long totalJobs;
    private long activeJobs;

    private long totalApplications;
    private long scheduledInterviews;
    private long offersSent;
    private long studentsHired;
}
