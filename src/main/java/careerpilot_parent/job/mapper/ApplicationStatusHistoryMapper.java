package careerpilot_parent.job.mapper;

import careerpilot_parent.job.dto.response.ApplicationStatusHistoryResponse;
import careerpilot_parent.job.entity.ApplicationStatusHistory;
import careerpilot_parent.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStatusHistoryMapper {

    public ApplicationStatusHistoryResponse toResponse(
            ApplicationStatusHistory history
    ) {
        if (history == null) {
            return null;
        }

        User changedBy = history.getChangedBy();

        return ApplicationStatusHistoryResponse.builder()
                .id(history.getId())
                .applicationId(
                        history.getApplication() == null
                                ? null
                                : history.getApplication().getId()
                )
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .changedByUserId(
                        changedBy == null ? null : changedBy.getId()
                )
                .changedByName(buildUserName(changedBy))
                .comment(history.getComment())
                .changedAt(history.getCreatedAt())
                .build();
    }

    private String buildUserName(User user) {
        if (user == null) {
            return null;
        }

        String firstName = normalize(user.getFirstName());
        String lastName = normalize(user.getLastName());

        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    private String normalize(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
