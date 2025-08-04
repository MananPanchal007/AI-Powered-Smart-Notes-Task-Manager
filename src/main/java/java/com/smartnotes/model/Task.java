package java.com.smartnotes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {
    
    @Id
    private String id;
    
    @DocumentReference
    private User user;
    
    @DocumentReference
    private Note note; // Optional: Link to the note this task was generated from
    
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private boolean aiGenerated;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
