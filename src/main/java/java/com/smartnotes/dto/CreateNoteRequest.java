package java.com.smartnotes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    String title,
    
    @NotBlank(message = "Content is required")
    String content,
    
    boolean archived
) {}
