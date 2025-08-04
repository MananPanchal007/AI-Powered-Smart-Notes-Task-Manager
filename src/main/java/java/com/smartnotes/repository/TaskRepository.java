package java.com.smartnotes.repository;

import com.smartnotes.model.Task;
import com.smartnotes.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    Flux<Task> findByUserAndDueDateAfterAndStatusNot(User user, LocalDateTime date, Task.TaskStatus status);
    Flux<Task> findByUserAndStatus(User user, Task.TaskStatus status);
    Flux<Task> findByUserAndAiGeneratedTrue(User user);
    Mono<Task> findByIdAndUser(String id, User user);
    Mono<Boolean> existsByIdAndUser(String id, User user);
    Mono<Long> countByUser(User user);
}
