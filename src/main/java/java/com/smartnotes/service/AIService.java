package java.com.smartnotes.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final OpenAiService openAiService;
    private final OpenAIConfig.OpenAIConfigProperties config;
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        // Create a virtual thread executor for non-blocking AI operations
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public Mono<String> generateSummary(String content) {
        String prompt = "Please provide a concise summary of the following text. Focus on the main points and key information.\n\n" + content;
        
        return generateText(prompt)
                .onErrorResume(e -> {
                    log.error("Error generating summary: {}", e.getMessage(), e);
                    return Mono.just("Unable to generate summary at this time. Please try again later.");
                });
    }

    public Mono<List<String>> generateTaskSuggestions(String content) {
        String prompt = "Based on the following text, generate a list of actionable tasks. " +
                "Each task should be a clear, actionable item that can be completed. " +
                "Return each task on a new line.\n\n" + content;
        
        return generateText(prompt)
                .map(response -> {
                    String[] tasks = response.split("\n");
                    List<String> taskList = new ArrayList<>();
                    for (String task : tasks) {
                        String trimmed = task.trim();
                        if (!trimmed.isEmpty() && !trimmed.matches("^\\d+[.)]\\s*")) {
                            taskList.add(trimmed);
                        }
                    }
                    return taskList;
                })
                .onErrorResume(e -> {
                    log.error("Error generating task suggestions: {}", e.getMessage(), e);
                    return Mono.just(List.of("Unable to generate tasks at this time. Please try again later."));
                });
    }

    private Mono<String> generateText(String prompt) {
        return Mono.fromCallable(() -> {
                    List<ChatMessage> messages = new ArrayList<>();
                    messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

                    ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                            .model(config.getModel())
                            .messages(messages)
                            .maxTokens(config.getMaxTokens())
                            .temperature(config.getTemperature())
                            .build();

                    return openAiService.createChatCompletion(completionRequest)
                            .getChoices()
                            .get(0)
                            .getMessage()
                            .getContent();
                })
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }
}
