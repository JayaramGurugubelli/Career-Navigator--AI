package career_Navigator_parent.user.service;

import career_Navigator_parent.user.dto.request.ChangePasswordRequest;
import career_Navigator_parent.user.dto.request.DeactivateAccountRequest;
import career_Navigator_parent.user.dto.request.UpdateProfileRequest;
import career_Navigator_parent.user.dto.request.UpdateSocialLinksRequest;
import career_Navigator_parent.user.dto.response.UserProfileResponse;
import career_Navigator_parent.user.dto.response.UserResponse;

public interface UserService {

    void changePassword(ChangePasswordRequest request);

    UserResponse getCurrentUser();
    UserProfileResponse getCurrentUserProfile();
    UserProfileResponse getUserProfile(Long userId);

    UserProfileResponse updateProfile(UpdateProfileRequest request);
    UserProfileResponse updateCurrentUserSocialLinks(UpdateSocialLinksRequest request);
    UserProfileResponse updateSocialLinks( UpdateSocialLinksRequest request);
    UserProfileResponse updateCurrentUserProfile(UpdateProfileRequest request);
    void uploadProfilePicture(String file);

    void deleteProfilePicture();

    void deactivateAccount( DeactivateAccountRequest request);
}