package career_Navigator_parent.security.service;

import career_Navigator_parent.security.model.CustomUserDetails;
import career_Navigator_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import career_Navigator_parent.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
//
//        User user = userRepository
//                .findByEmailOrUsername(usernameOrEmail, usernameOrEmail)
//                .orElseThrow(() -> new UsernameNotFoundException(
//                                "User not found with email or username : " + usernameOrEmail
//                        ));
//
//        return new CustomUserDetails(user);
//    }
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {

        User user = userRepository.findByEmailOrUsernameWithRoles(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println("User: " + user.getUsername());
        System.out.println("Roles count: " + user.getRoles().size());

        user.getRoles().forEach(role ->
                System.out.println("Role = " + role.getRole().getName()));

        return new CustomUserDetails(user);
    }
}
