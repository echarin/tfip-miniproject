package ibf2022.tfipminiproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
public class TfipMiniprojectApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TfipMiniprojectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}
}
