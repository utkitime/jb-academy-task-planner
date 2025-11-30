package taskmanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
        String id,
        String title,
        String description,
        String status,
        String author,
        String assignee,
        @JsonProperty("total_comments")
        Integer totalComments
) {
}
