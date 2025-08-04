package java.com.smartnotes.service;

import com.smartnotes.dto.CreateTaskRequest;
import com.smartnotes.dto.TaskDto;
import com.smartnotes.exception.ResourceNotFoundException;
import com.smartnotes.model.Note;
import com.smartnotes.model.Task;
import com.smartnotes.model.User;
import java.com.smartnotes.repository.NoteRepository;
import java.com.smartnotes.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Flux<TaskDto> getUserTasks(Task.TaskStatus status) {
        return securityUtils.getCurrentUser()
                .flatMapMany(user -> {
                    if (status != null) {
                        return taskRepository.findByUserAndStatus(user, status);
                    } else {
                        return taskRepository.findByUser(user);
                    }
                })
                .map(TaskDto::fromEntity);
    }

    @Override
    public Mono<TaskDto> getTaskById(String id) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> taskRepository.findByIdAndUser(id, user))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found with id: " + id)))
                .map(TaskDto::fromEntity);
    }

    @Override
    @Transactional
    public Mono<TaskDto> createTask(CreateTaskRequest request) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> {
                    Task task = Task.builder()
                            .user(user)
                            .description(request.description())
                            .dueDate(request.dueDate())
                            .status(request.status())
                            .aiGenerated(false)
                            .createdAt(LocalDateTime.now())
                            .build();

                    // If noteId is provided, fetch and associate the note
                    if (request.noteId() != null && !request.noteId().isBlank()) {
                        return noteRepository.findByIdAndUser(request.noteId(), user)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + request.noteId())))
                                .flatMap(note -> {
                                    task.setNote(note);
                                    return taskRepository.save(task);
                                });
                    }
                    return taskRepository.save(task);
                })
                .map(TaskDto::fromEntity);
    }

    @Override
    @Transactional
    public Mono<TaskDto> updateTask(String id, CreateTaskRequest request) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> taskRepository.findByIdAndUser(id, user)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found with id: " + id)))
                        .flatMap(task -> {
                            task.setDescription(request.description());
                            task.setDueDate(request.dueDate());
                            task.setStatus(request.status());
                            
                            // Update note reference if noteId is provided
                            if (request.noteId() != null && !request.noteId().isBlank()) {
                                return noteRepository.findByIdAndUser(request.noteId(), user)
                                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + request.noteId())))
                                        .flatMap(note -> {
                                            task.setNote(note);
                                            return taskRepository.save(task);
                                        });
                            } else {
                                task.setNote(null);
                                return taskRepository.save(task);
                            }
                        })
                )
                .map(TaskDto::fromEntity);
    }

    @Override
    @Transactional
    public Mono<Void> deleteTask(String id) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> taskRepository.deleteByIdAndUser(id, user));
    }

    @Override
    @Transactional
    public Mono<TaskDto> updateTaskStatus(String id, Task.TaskStatus status) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> taskRepository.findByIdAndUser(id, user)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found with id: " + id)))
                        .flatMap(task -> {
                            task.setStatus(status);
                            return taskRepository.save(task);
                        })
                )
                .map(TaskDto::fromEntity);
    }

    private final AIService aiService;
    private final NoteService noteService;

    @Override
    @Transactional
    public Flux<TaskDto> generateTasksFromNote(String noteId) {
        return securityUtils.getCurrentUser()
                .flatMapMany(user -> noteRepository.findByIdAndUser(noteId, user)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + noteId)))
                        .flatMapMany(note -> {
                            if (note.getContent() == null || note.getContent().isBlank()) {
                                return Flux.error(new IllegalArgumentException("Note content is empty"));
                            }
                            
                            return aiService.generateTaskSuggestions(note.getContent())
                                    .flatMapMany(tasks -> {
                                        if (tasks.isEmpty()) {
                                            return Flux.empty();
                                        }
                                        
                                        return Flux.fromIterable(tasks)
                                                .flatMap(taskDescription -> {
                                                    Task task = Task.builder()
                                                            .user(user)
                                                            .note(note)
                                                            .description(taskDescription)
                                                            .status(Task.TaskStatus.TODO)
                                                            .aiGenerated(true)
                                                            .createdAt(LocalDateTime.now())
                                                            .build();
                                                    
                                                    return taskRepository.save(task);
                                                });
                                    });
                        })
                )
                .map(TaskDto::fromEntity);
    }
}
