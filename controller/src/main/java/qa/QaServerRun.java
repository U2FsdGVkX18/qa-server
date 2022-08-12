package qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QaServerRun {
    public static void main(String[] args) {
        SpringApplication.run(QaServerRun.class, args);
    }
}
