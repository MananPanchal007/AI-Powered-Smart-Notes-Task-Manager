package java.com.smartnotes.service;

import com.smartnotes.dto.CreateNoteRequest;
import com.smartnotes.dto.NoteDto;
import com.smartnotes.model.Note;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NoteService {
    Flux<NoteDto> getUserNotes(boolean includeArchived);
    Mono<NoteDto> getNoteById(String id);
    Mono<NoteDto> createNote(CreateNoteRequest request);
    Mono<NoteDto> updateNote(String id, CreateNoteRequest request);
    Mono<Void> deleteNote(String id);
    Mono<NoteDto> archiveNote(String id, boolean archive);
    Mono<String> generateSummary(String noteId);
}
