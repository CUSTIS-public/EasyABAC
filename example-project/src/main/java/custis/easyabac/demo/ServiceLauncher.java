package custis.easyabac.demo;

import custis.easyabac.demo.config.ServiceDefaultApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ServiceDefaultApplicationContext.class)
public class ServiceLauncher {
    public static void main(String[] args) {
        SpringApplication.run(ServiceLauncher.class, args);
    }
}
