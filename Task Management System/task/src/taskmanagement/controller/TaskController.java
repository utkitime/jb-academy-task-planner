package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.*;
import taskmanagement.model.Comment;
import taskmanagement.model.Task;
import taskmanagement.service.CommentService;
import taskmanagement.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TaskController {
    private final TaskService taskService;
    private final CommentService commentService;

    public TaskController(TaskService taskService, CommentService commentService) {
        this.taskService = taskService;
        this.commentService = commentService;
    }

    @PostMapping(path = "/api/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Task task = taskService.createTask(request, username);

        return ResponseEntity.ok(toResponse(task, false));
    }

    @GetMapping(path = "/api/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee) {

        List<Task> tasks = taskService.getAllTasks(author, assignee);

        List<TaskResponse> response = tasks.stream()
                .map(task -> toResponse(task, true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/api/tasks/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Integer taskId,
            @Valid @RequestBody TaskAssignRequest request,
            Authentication authentication) {

        Task task = taskService.assignTask(taskId, request.assignee(), authentication.getName());

        return ResponseEntity.ok(toResponse(task, false));
    }

    @PutMapping(path = "/api/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> changeStatus(
            @PathVariable Integer taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            Authentication authentication) {

        Task task = taskService.changeStatus(taskId, request, authentication.getName());

        return ResponseEntity.ok(toResponse(task, false));
    }

    @PostMapping(path = "/api/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Integer taskId,
            @Valid @RequestBody CommentCreateRequest request,
            Authentication authentication) {

        Comment comment = commentService.createComment(taskId, request.text(), authentication.getName());
        return ResponseEntity.ok(toResponse(comment));
    }

    @GetMapping(path = "/api/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Integer taskId) {

        List<CommentResponse> response = commentService.getComments(taskId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private TaskResponse toResponse(Task task, boolean includeTotalComments) {
        return new TaskResponse(
                task.getId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor().getUsername(),
                task.getAssignee(),
                includeTotalComments ? task.getTotalComments() : null
        );
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId().toString(),
                comment.getTask().getId().toString(),
                comment.getText(),
                comment.getAuthor().getUsername()
        );
    }
}
