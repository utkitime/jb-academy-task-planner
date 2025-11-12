package taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreateRequest(
    @NotNull(message = "Title is required")
    @NotBlank(message = "Title must not be blank")
    String title,

    @NotNull(message = "Description is required")
    @NotBlank(message = "Description must not be blank")
    String description
) {
}