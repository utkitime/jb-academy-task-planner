package taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskAssignRequest(
        @NotNull(message = "Assignee is required")
        @NotBlank(message = "Assignee is required")
        String assignee
) {
}
