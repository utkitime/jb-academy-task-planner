package taskmanagement.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import taskmanagement.model.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer>, PagingAndSortingRepository<Task, Integer> {
    List<Task> findByAuthorUsernameIgnoreCase(String username, Sort sort);
    List<Task> findByAssigneeIgnoreCase(String assignee, Sort sort);
    List<Task> findByAuthorUsernameIgnoreCaseAndAssigneeIgnoreCase(String username, String assignee, Sort sort);
}
