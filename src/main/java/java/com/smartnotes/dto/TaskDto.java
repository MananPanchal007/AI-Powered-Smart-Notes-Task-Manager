package java.com.smartnotes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.com.smartnotes.model.Task;

import java.time.LocalDateTime;

public record TaskDto(
    String id,
    String noteId,
    String description,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dueDate,
    Task.TaskStatus status,
    boolean aiGenerated,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt
) {
    public static TaskDto fromEntity(Task task) {
        return new TaskDto(
            task.getId(),
            task.getNote() != null ? task.getNote().getId() : null,
            task.getDescription(),
            task.getDueDate(),
            task.getStatus(),
            task.isAiGenerated(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
