package taskmanagement.service;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import taskmanagement.dto.TaskCreateRequest;
import taskmanagement.dto.TaskStatusUpdateRequest;
import taskmanagement.model.Task;
import taskmanagement.model.User;
import taskmanagement.repository.TaskRepository;
import taskmanagement.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Set<String> ALLOWED_STATUSES = Set.of("CREATED", "IN_PROGRESS", "COMPLETED");

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(TaskCreateRequest request, String username) {
        User author = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setAuthor(author);
        task.setStatus("CREATED");

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(String author, String assignee) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        boolean hasAuthor = author != null && !author.trim().isEmpty();
        boolean hasAssignee = assignee != null && !assignee.trim().isEmpty();

        if (hasAuthor && hasAssignee) {
            return taskRepository.findByAuthorUsernameIgnoreCaseAndAssigneeIgnoreCase(author.trim(), assignee.trim(), sort);
        }

        if (hasAuthor) {
            return taskRepository.findByAuthorUsernameIgnoreCase(author, sort);
        }

        if (hasAssignee) {
            return taskRepository.findByAssigneeIgnoreCase(assignee.trim(), sort);
        }

        return taskRepository.findAll(sort);
    }

    public Task assignTask(Integer taskId, String assignee, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (!username.equalsIgnoreCase(task.getAuthor().getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can assign tasks");
        }

        if (assignee == null || assignee.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee is required");
        }

        String normalized = assignee.trim();

        if ("none".equalsIgnoreCase(normalized)) {
            task.setAssignee(null);
            return taskRepository.save(task);
        }

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid assignee email");
        }

        String normalizedEmail = normalized.toLowerCase();
        User user = userRepository.findUserByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));

        task.setAssignee(user.getEmail());
        return taskRepository.save(task);
    }

    public Task changeStatus(Integer taskId, TaskStatusUpdateRequest request, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (request.status() == null || request.status().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        String normalizedStatus = request.status().trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        String normalizedUsername = username.toLowerCase();
        boolean isAuthor = normalizedUsername.equalsIgnoreCase(task.getAuthor().getUsername());
        String taskAssignee = task.getAssignee();
        boolean isAssignee = taskAssignee != null
                && !"none".equalsIgnoreCase(taskAssignee)
                && normalizedUsername.equalsIgnoreCase(taskAssignee);

        if (!isAuthor && !isAssignee) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to change status");
        }

        task.setStatus(normalizedStatus);
        return taskRepository.save(task);
    }
}
