package career_Navigator_parent.jobrecommendation.scheduler;

import career_Navigator_parent.jobrecommendation.service.JobRecommendationService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class JobRecommendationScheduler {

    private final JobRecommendationService
            jobRecommendationService;

    /*
     * Prevents overlapping execution in one application
     * instance.
     */
    private final AtomicBoolean refreshRunning =
            new AtomicBoolean(false);

    /*
     * Runs every day at 2:00 AM.
     */
    @Scheduled(
            cron = "${app.recommendation.refresh-cron:0 0 2 * * *}",
            zone = "${app.time-zone:Asia/Kolkata}"
    )
    public void refreshRecommendations() {

        if (!refreshRunning.compareAndSet(
                false,
                true
        )) {
            return;
        }

        try {
            int refreshedStudents =
                    jobRecommendationService
                            .refreshAllActiveStudents();

            System.out.println(
                    "Job recommendations refreshed for "
                            + refreshedStudents
                            + " active students."
            );

        } catch (RuntimeException exception) {

            System.err.println(
                    "Scheduled recommendation refresh failed: "
                            + exception.getMessage()
            );

        } finally {
            refreshRunning.set(false);
        }
    }

    /*
     * Runs every hour to deactivate expired cache rows.
     */
    @Scheduled(
            cron = "${app.recommendation.expiry-cron:0 0 * * * *}",
            zone = "${app.time-zone:Asia/Kolkata}"
    )
    public void deactivateExpiredRecommendations() {

        try {
            jobRecommendationService
                    .deactivateExpiredRecommendations();

        } catch (RuntimeException exception) {

            System.err.println(
                    "Expired recommendation cleanup failed: "
                            + exception.getMessage()
            );
        }
    }
}