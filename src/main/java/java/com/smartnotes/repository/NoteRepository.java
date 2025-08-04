package java.com.smartnotes.repository;

import com.smartnotes.model.Note;
import com.smartnotes.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NoteRepository extends ReactiveMongoRepository<Note, String> {
    Flux<Note> findByUserAndArchivedFalseAndDeletedFalse(User user);
    Flux<Note> findByUserAndArchivedTrueAndDeletedFalse(User user);
    Mono<Note> findByIdAndUser(String id, User user);
    Mono<Boolean> existsByIdAndUser(String id, User user);
    Mono<Long> countByUser(User user);
}
