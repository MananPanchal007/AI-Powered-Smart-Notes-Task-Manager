package java.com.smartnotes.service;

import com.smartnotes.dto.CreateNoteRequest;
import com.smartnotes.dto.NoteDto;
import com.smartnotes.exception.ResourceNotFoundException;
import com.smartnotes.model.Note;
import com.smartnotes.model.User;
import java.com.smartnotes.repository.NoteRepository;
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
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Override
    public Flux<NoteDto> getUserNotes(boolean includeArchived) {
        return securityUtils.getCurrentUser()
                .flatMapMany(user -> {
                    if (includeArchived) {
                        return noteRepository.findByUserAndDeletedFalse(user);
                    } else {
                        return noteRepository.findByUserAndArchivedFalseAndDeletedFalse(user);
                    }
                })
                .map(NoteDto::fromEntity);
    }

    @Override
    public Mono<NoteDto> getNoteById(String id) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> noteRepository.findByIdAndUser(id, user))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + id)))
                .map(NoteDto::fromEntity);
    }

    @Override
    @Transactional
    public Mono<NoteDto> createNote(CreateNoteRequest request) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> {
                    Note note = Note.builder()
                            .user(user)
                            .title(request.title())
                            .content(request.content())
                            .archived(request.archived())
                            .createdAt(LocalDateTime.now())
                            .deleted(false)
                            .build();
                    return noteRepository.save(note);
                })
                .map(NoteDto::fromEntity);
    }

    @Override
    @Transactional
    public Mono<NoteDto> updateNote(String id, CreateNoteRequest request) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> noteRepository.findByIdAndUser(id, user)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + id)))
                        .flatMap(note -> {
                            note.setTitle(request.title());
                            note.setContent(request.content());
                            note.setArchived(request.archived());
                            return noteRepository.save(note);
                        })
                )
                .map(NoteDto::fromEntity);
    }

    @Override
    @Transactional
    public Mono<Void> deleteNote(String id) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> noteRepository.findByIdAndUser(id, user)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + id)))
                        .flatMap(note -> {
                            note.setDeleted(true);
                            return noteRepository.save(note).then();
                        })
                );
    }

    @Override
    @Transactional
    public Mono<NoteDto> archiveNote(String id, boolean archive) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> noteRepository.findByIdAndUser(id, user)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + id)))
                        .flatMap(note -> {
                            note.setArchived(archive);
                            return noteRepository.save(note);
                        })
                )
                .map(NoteDto::fromEntity);
    }

    private final AIService aiService;

    @Override
    @Transactional
    public Mono<String> generateSummary(String noteId) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> noteRepository.findByIdAndUser(noteId, user)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Note not found with id: " + noteId)))
                        .flatMap(note -> {
                            if (note.getContent() == null || note.getContent().isBlank()) {
                                return Mono.just("Note is empty");
                            }
                            return aiService.generateSummary(note.getContent())
                                    .flatMap(summary -> {
                                        note.setSummary(summary);
                                        return noteRepository.save(note)
                                                .thenReturn(summary);
                                    });
                        })
                );
    }
}
