package taskmanagement.dto;

public record TaskResponse(
    String id,
    String title,
    String description,
    String status,
    String author
) {
}