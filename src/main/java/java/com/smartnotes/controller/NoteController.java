package java.com.smartnotes.controller;

import com.smartnotes.dto.CreateNoteRequest;
import com.smartnotes.dto.NoteDto;
import com.smartnotes.service.NoteService;
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
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "APIs for managing notes")
@SecurityRequirement(name = "bearerAuth")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all notes", description = "Retrieve all notes for the current user")
    public Flux<NoteDto> getAllNotes(
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived
    ) {
        return noteService.getUserNotes(includeArchived);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get note by ID", description = "Retrieve a specific note by its ID")
    public Mono<NoteDto> getNoteById(@PathVariable String id) {
        return noteService.getNoteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new note", description = "Create a new note with the provided details")
    public Mono<NoteDto> createNote(@Valid @RequestBody CreateNoteRequest request) {
        return noteService.createNote(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a note", description = "Update an existing note with the provided details")
    public Mono<NoteDto> updateNote(
            @PathVariable String id,
            @Valid @RequestBody CreateNoteRequest request
    ) {
        return noteService.updateNote(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a note", description = "Delete a note by its ID")
    public Mono<Void> deleteNote(@PathVariable String id) {
        return noteService.deleteNote(id);
    }

    @PostMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Archive/Unarchive a note", description = "Archive or unarchive a note by its ID")
    public Mono<NoteDto> archiveNote(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "true") boolean archive
    ) {
        return noteService.archiveNote(id, archive);
    }

    @PostMapping("/{id}/summarize")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate a summary", description = "Generate a summary for a note using AI")
    public Mono<String> generateSummary(@PathVariable String id) {
        return noteService.generateSummary(id);
    }
}
