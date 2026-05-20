package com.example.resilient_api;

import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.domain.usecase.TechnologyUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = TechnologyApplication.class)
class TechnologyApplicationTests {

	@MockBean
	private TechnologyPersistencePort userPersistencePort;

	@Autowired
	private TechnologyUseCase userUseCase;

	@Test
	void contextLoads() {
	}

}
