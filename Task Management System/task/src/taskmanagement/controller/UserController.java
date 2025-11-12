package taskmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.RegistrationRequest;
import taskmanagement.dto.UserRegistrationDto;
import taskmanagement.exception.UserAlreadyExistsException;
import taskmanagement.service.UserDetailsServiceImpl;

import java.util.regex.Pattern;

@RestController
public class UserController {
    private final UserDetailsServiceImpl userService;

    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @PostMapping(path = "/api/accounts")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        // Check if email is missing, empty, or blank
        if (request.email() == null || request.email().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }

        // Check if password is missing, empty, or blank
        if (request.password() == null || request.password().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required");
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(request.email()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format");
        }

        // Validate password length (at least 6 characters)
        if (request.password().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 6 characters long");
        }

        // Create DTO and call service
        try {
            String normalizedEmail = request.email().toLowerCase();
            UserRegistrationDto dto = new UserRegistrationDto(request.password(), normalizedEmail);
            userService.register(dto);
            return ResponseEntity.ok("New user successfully registered");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}