package taskmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    @GetMapping(path = "/api/tasks")
    public ResponseEntity<String> getTasks(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Tasks for user: " + username);
    }
}
