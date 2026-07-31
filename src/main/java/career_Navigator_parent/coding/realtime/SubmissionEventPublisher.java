package career_Navigator_parent.coding.realtime;

public interface SubmissionEventPublisher {
    void publish(Long studentId, SubmissionEvent event);
}
