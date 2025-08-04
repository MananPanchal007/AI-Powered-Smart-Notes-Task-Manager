package java.com.smartnotes.service;

import com.smartnotes.dto.CreateTaskRequest;
import com.smartnotes.dto.TaskDto;
import com.smartnotes.model.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {
    Flux<TaskDto> getUserTasks(Task.TaskStatus status);
    Mono<TaskDto> getTaskById(String id);
    Mono<TaskDto> createTask(CreateTaskRequest request);
    Mono<TaskDto> updateTask(String id, CreateTaskRequest request);
    Mono<Void> deleteTask(String id);
    Mono<TaskDto> updateTaskStatus(String id, Task.TaskStatus status);
    Flux<TaskDto> generateTasksFromNote(String noteId);
}
