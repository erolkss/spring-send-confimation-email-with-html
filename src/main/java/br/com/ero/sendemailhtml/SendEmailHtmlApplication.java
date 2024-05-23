package br.com.ero.sendemailhtml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SendEmailHtmlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SendEmailHtmlApplication.class, args);
	}

}
