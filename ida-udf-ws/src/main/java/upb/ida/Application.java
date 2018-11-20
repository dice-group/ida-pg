package upb.ida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new File(System.getProperty("user.dir") + "/uploads").mkdir();
        SpringApplication.run(Application.class, args);
    }
}