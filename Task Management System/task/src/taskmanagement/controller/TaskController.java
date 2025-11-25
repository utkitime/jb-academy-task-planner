package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.TaskCreateRequest;
import taskmanagement.dto.TaskAssignRequest;
import taskmanagement.dto.TaskStatusUpdateRequest;
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
                task.getAuthor().getUsername(),
                task.getAssignee()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/api/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee) {

        List<Task> tasks = taskService.getAllTasks(author, assignee);

        List<TaskResponse> response = tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId().toString(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getAuthor().getUsername(),
                        task.getAssignee()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/api/tasks/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Integer taskId,
            @Valid @RequestBody TaskAssignRequest request,
            Authentication authentication) {

        Task task = taskService.assignTask(taskId, request.assignee(), authentication.getName());

        TaskResponse response = new TaskResponse(
                task.getId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor().getUsername(),
                task.getAssignee()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/api/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> changeStatus(
            @PathVariable Integer taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            Authentication authentication) {

        Task task = taskService.changeStatus(taskId, request, authentication.getName());

        TaskResponse response = new TaskResponse(
                task.getId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor().getUsername(),
                task.getAssignee()
        );

        return ResponseEntity.ok(response);
    }
}
