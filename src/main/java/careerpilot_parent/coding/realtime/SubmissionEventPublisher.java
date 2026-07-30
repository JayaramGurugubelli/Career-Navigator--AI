package careerpilot_parent.coding.realtime;

public interface SubmissionEventPublisher {
    void publish(Long studentId, SubmissionEvent event);
}
