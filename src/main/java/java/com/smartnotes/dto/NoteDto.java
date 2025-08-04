package java.com.smartnotes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.com.smartnotes.model.Note;

import java.time.LocalDateTime;

public record NoteDto(
    String id,
    String title,
    String content,
    String summary,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt,
    boolean archived
) {
    public static NoteDto fromEntity(Note note) {
        return new NoteDto(
            note.getId(),
            note.getTitle(),
            note.getContent(),
            note.getSummary(),
            note.getCreatedAt(),
            note.getUpdatedAt(),
            note.isArchived()
        );
    }
}
