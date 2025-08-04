package java.com.smartnotes.dto;

import java.com.smartnotes.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskRequest(
    String noteId,
    
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    String description,
    
    LocalDateTime dueDate,
    
    @NotNull(message = "Status is required")
    Task.TaskStatus status
) {}
