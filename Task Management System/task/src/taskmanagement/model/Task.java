package taskmanagement.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue
    private Integer id;

    private String title;
    private String description;
    private String status;
    private String assignee;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignee() {
        return Objects.requireNonNullElse(assignee, "none");
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}