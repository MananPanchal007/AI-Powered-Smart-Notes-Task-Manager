package java.com.smartnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartNotesManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartNotesManagerApplication.class, args);
    }
}
