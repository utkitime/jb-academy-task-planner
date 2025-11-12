package taskmanagement.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taskmanagement.dto.UserRegistrationDto;
import taskmanagement.exception.UserAlreadyExistsException;
import taskmanagement.model.User;
import taskmanagement.repository.UserRepository;
import taskmanagement.security.UserAdapter;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRegistrationDto dto) {
        String normalizedEmail = dto.getEmail().toLowerCase();

        if (isUserExists(normalizedEmail)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = new User();
        // TODO: сделать отдельный username
        user.setUsername(normalizedEmail);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAuthority("ROLE_USER");
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = username.toLowerCase();
        User user = userRepository
                .findUserByUsername(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return new UserAdapter(user);
    }

    private boolean isUserExists(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }
}
