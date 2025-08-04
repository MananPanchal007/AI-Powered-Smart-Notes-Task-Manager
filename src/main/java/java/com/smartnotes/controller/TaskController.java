package java.com.smartnotes.controller;

import com.smartnotes.dto.CreateTaskRequest;
import com.smartnotes.dto.TaskDto;
import com.smartnotes.model.Task;
import com.smartnotes.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "APIs for managing tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all tasks", description = "Retrieve all tasks for the current user, optionally filtered by status")
    public Flux<TaskDto> getAllTasks(
            @RequestParam(required = false) Task.TaskStatus status
    ) {
        return taskService.getUserTasks(status);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    public Mono<TaskDto> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task", description = "Create a new task with the provided details")
    public Mono<TaskDto> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a task", description = "Update an existing task with the provided details")
    public Mono<TaskDto> updateTask(
            @PathVariable String id,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task", description = "Delete a task by its ID")
    public Mono<Void> deleteTask(@PathVariable String id) {
        return taskService.deleteTask(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update task status", description = "Update the status of a task")
    public Mono<TaskDto> updateTaskStatus(
            @PathVariable String id,
            @RequestParam Task.TaskStatus status
    ) {
        return taskService.updateTaskStatus(id, status);
    }

    @PostMapping("/generate-from-note/{noteId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate tasks from note", description = "Generate tasks from a note using AI")
    public Flux<TaskDto> generateTasksFromNote(@PathVariable String noteId) {
        return taskService.generateTasksFromNote(noteId);
    }
}
