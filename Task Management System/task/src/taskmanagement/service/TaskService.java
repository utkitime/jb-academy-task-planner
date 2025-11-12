package taskmanagement.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taskmanagement.dto.TaskCreateRequest;
import taskmanagement.model.Task;
import taskmanagement.model.User;
import taskmanagement.repository.TaskRepository;
import taskmanagement.repository.UserRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

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

    public List<Task> getAllTasks(String author) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        if (author != null && !author.trim().isEmpty()) {
            return taskRepository.findByAuthorUsernameIgnoreCase(author, sort);
        }

        return taskRepository.findAll(sort);
    }
}