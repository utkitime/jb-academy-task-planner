package taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank(message = "Comment text must not be blank")
        String text
) {
}
