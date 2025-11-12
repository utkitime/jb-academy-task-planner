package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.TaskCreateRequest;
import taskmanagement.dto.TaskResponse;
import taskmanagement.model.Task;
import taskmanagement.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(path = "/api/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Task task = taskService.createTask(request, username);

        TaskResponse response = new TaskResponse(
                task.getId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor().getUsername()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/api/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) String author) {

        List<Task> tasks = taskService.getAllTasks(author);

        List<TaskResponse> response = tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId().toString(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getAuthor().getUsername()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
