package br.edu.lampi.infrareport;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;


@SpringBootTest(classes = InfrareportApplication.class,
				webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:test.properties")
class InfrareportApplicationTests {
	@Autowired
	protected TestRestTemplate restTemplate;
	
	@LocalServerPort
	private int port;

	protected String getRootUrl() {
		return "http://localhost:" + port;
	}

	@Test
	public void contextLoads() {
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}
