package career_Navigator_parent.security.util;

import career_Navigator_parent.security.model.CustomUserDetails;
import career_Navigator_parent.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("No authenticated user found.");
        }

        return userDetails;
    }

    public Long getCurrentUserId() {
        return getCurrentUserDetails().getUserId();
    }

    public User getCurrentUser() {
        return getCurrentUserDetails().getUser();
    }
}