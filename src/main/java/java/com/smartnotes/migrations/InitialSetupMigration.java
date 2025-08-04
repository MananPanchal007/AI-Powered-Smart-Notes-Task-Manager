package java.com.smartnotes.migrations;

import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.api.config.IndependentMongockConfiguration;
import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;
import reactor.core.publisher.Mono;

@ChangeUnit(id = "initial-setup", order = "001", author = "system")
public class InitialSetupMigration {

    private final ReactiveMongoTemplate mongoTemplate;
    private final MongoDatabase mongoDatabase;

    public InitialSetupMigration(ReactiveMongoTemplate mongoTemplate, MongoDatabase mongoDatabase) {
        this.mongoTemplate = mongoTemplate;
        this.mongoDatabase = mongoDatabase;
    }

    @Execution
    public void execute() {
        // Create collections with validation if they don't exist
        createCollectionWithValidation("users", createUserValidationSchema());
        createCollectionWithValidation("notes", createNoteValidationSchema());
        createCollectionWithValidation("tasks", createTaskValidationSchema());

        // Create indexes
        createUserIndexes();
        createNoteIndexes();
        createTaskIndexes();
    }

    @RollbackExecution
    public void rollback() {
        // In a real application, you might want to implement rollback logic
    }

    private void createCollectionWithValidation(String collectionName, MongoJsonSchema schema) {
        mongoTemplate.collectionExists(collectionName)
                .flatMap(exists -> {
                    if (!exists) {
                        CollectionOptions options = CollectionOptions.empty()
                                .validator(Validator.schema(schema))
                                .size(1024 * 1024 * 10) // 10MB
                                .maxDocuments(10000);
                        return mongoTemplate.createCollection(collectionName, options);
                    }
                    return Mono.empty();
                })
                .block();
    }

    private MongoJsonSchema createUserValidationSchema() {
        return MongoJsonSchema.builder()
                .required("email", "name", "createdAt")
                .properties(
                        JsonSchemaProperty.string("email").pattern("^[^@]+@[^@]+\\.[^@]+$"),
                        JsonSchemaProperty.string("name").minLength(1).maxLength(100),
                        JsonSchemaProperty.string("picture").description("URL to user's profile picture").nullable(true),
                        JsonSchemaProperty.bool("emailVerified"),
                        JsonSchemaProperty.date("createdAt"),
                        JsonSchemaProperty.date("updatedAt").nullable(true),
                        JsonSchemaProperty.bool("active")
                ).build();
    }

    private MongoJsonSchema createNoteValidationSchema() {
        return MongoJsonSchema.builder()
                .required("user", "content", "createdAt")
                .properties(
                        JsonSchemaProperty.objectId("user"),
                        JsonSchemaProperty.string("title").minLength(1).maxLength(200),
                        JsonSchemaProperty.string("content").minLength(1),
                        JsonSchemaProperty.string("summary").nullable(true),
                        JsonSchemaProperty.date("createdAt"),
                        JsonSchemaProperty.date("updatedAt").nullable(true),
                        JsonSchemaProperty.bool("archived"),
                        JsonSchemaProperty.bool("deleted")
                ).build();
    }

    private MongoJsonSchema createTaskValidationSchema() {
        return MongoJsonSchema.builder()
                .required("user", "description", "status", "createdAt")
                .properties(
                        JsonSchemaProperty.objectId("user"),
                        JsonSchemaProperty.objectId("note").nullable(true),
                        JsonSchemaProperty.string("description").minLength(1).maxLength(1000),
                        JsonSchemaProperty.date("dueDate").nullable(true),
                        JsonSchemaProperty.string("status").possibleValues("TODO", "IN_PROGRESS", "COMPLETED", "CANCELLED"),
                        JsonSchemaProperty.bool("aiGenerated"),
                        JsonSchemaProperty.date("createdAt"),
                        JsonSchemaProperty.date("updatedAt").nullable(true)
                ).build();
    }

    private void createUserIndexes() {
        mongoTemplate.indexOps("users").ensureIndex(
                new CompoundIndexDefinition(new Document("email", 1))
                        .unique()
                        .named("uniqueEmail")
        ).block();
    }

    private void createNoteIndexes() {
        mongoTemplate.indexOps("notes").ensureIndex(
                new CompoundIndexDefinition(new Document("user", 1).append("createdAt", -1))
                        .named("userNotes")
        ).block();

        mongoTemplate.indexOps("notes").ensureIndex(
                new CompoundIndexDefinition(new Document("user", 1).append("archived", 1).append("deleted", 1))
                        .named("userActiveNotes")
        ).block();
    }

    private void createTaskIndexes() {
        mongoTemplate.indexOps("tasks").ensureIndex(
                new CompoundIndexDefinition(new Document("user", 1).append("dueDate", -1))
                        .named("userTasksByDueDate")
        ).block();

        mongoTemplate.indexOps("tasks").ensureIndex(
                new CompoundIndexDefinition(new Document("user", 1).append("status", 1).append("dueDate", 1))
                        .named("userTasksByStatusAndDueDate")
        ).block();
    }
}
