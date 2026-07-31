package career_Navigator_parent.user.mapper;

import career_Navigator_parent.shared.enums.Gender;
import career_Navigator_parent.auth.dto.request.RegisterRequest;
import career_Navigator_parent.user.dto.request.UpdateProfileRequest;
import career_Navigator_parent.user.dto.request.UpdateSocialLinksRequest;
import career_Navigator_parent.user.dto.response.UserProfileResponse;
import career_Navigator_parent.user.dto.response.UserResponse;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest registerRequest) {
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setUsername(registerRequest.getUsername());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        return user;
    }
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .accountStatus(user.getAccountStatus())
                .emailVerified(user.getEmailVerified())
                .mobileVerified(user.getMobileVerified())
                .enabled(user.getEnabled())
                .profilePicture(user.getProfilePicture())
                .headline(user.getHeadline())
                .bio(user.getBio())
                .build();
    }
    public UserProfileResponse toUserProfileResponse(UserProfile profile) {

        return UserProfileResponse.builder()
                .dateOfBirth(profile.getDateOfBirth() == null ? null : profile.getDateOfBirth().toLocalDate())
                .gender(profile.getGender() == null ? null : profile.getGender().name())
                .city(profile.getCity())
                .state(profile.getState())
                .country(profile.getCountry())
                .address(profile.getAddress())
                .linkedInUrl(profile.getLinkedInUrl())
                .githubUrl(profile.getGithubUrl())
                .portfolioUrl(profile.getPortfolioUrl())
                .resumePath(profile.getResumePath())
                .leetCodeUrl(profile.getLeetCodeUrl())
                .codeforcesUrl(profile.getCodeforcesUrl())
                .hackerRankUrl(profile.getHackerRankUrl())
                .codeChefUrl(profile.getCodeChefUrl())
                .build();
    }
    public void updateUserProfile(UserProfile profile, UpdateProfileRequest request) {
        profile.setDateOfBirth(request.getDateOfBirth() == null ? null : request.getDateOfBirth().atStartOfDay());
        profile.setGender(request.getGender() == null ? null : Gender.valueOf(request.getGender()));
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setCountry(request.getCountry());
        profile.setAddress(request.getAddress());
    }

    public void updateSocialLinks(UserProfile profile, UpdateSocialLinksRequest request) {

        profile.setLinkedInUrl(request.getLinkedInUrl());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setLeetCodeUrl(request.getLeetCodeUrl());
        profile.setCodeforcesUrl(request.getCodeforcesUrl());
        profile.setHackerRankUrl(request.getHackerRankUrl());
        profile.setCodeChefUrl(request.getCodeChefUrl());
    }
    public void updateUser(User user, UpdateProfileRequest request) {

        user.setHeadline(request.getHeadline());
        user.setBio(request.getBio());
    }
}
