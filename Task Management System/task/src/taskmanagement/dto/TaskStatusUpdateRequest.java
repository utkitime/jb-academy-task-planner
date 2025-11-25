package taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
        @NotNull(message = "Status is required")
        @NotBlank(message = "Status must not be blank")
        String status
) {
}
