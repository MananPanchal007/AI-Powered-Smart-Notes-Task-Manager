package java.com.smartnotes.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class OpenAIConfig {

    private final OpenAIConfigProperties properties;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(properties.getApiKey(), Duration.ofSeconds(properties.getTimeoutSeconds()));
    }

    @Configuration
    @ConfigurationProperties(prefix = "app.openai")
    @RequiredArgsConstructor
    public static class OpenAIConfigProperties {
        private String apiKey;
        private int timeoutSeconds = 30;
        private String model = "gpt-3.5-turbo-instruct";
        private int maxTokens = 500;
        private double temperature = 0.7;

        // Getters and setters
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
    }
}
