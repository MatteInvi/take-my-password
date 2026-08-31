package myproject.takemypassword.take_my_password;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "myproject.takemypassword.take_my_password.repository")
public class TakeMyPasswordApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeMyPasswordApplication.class, args);
	}

}
