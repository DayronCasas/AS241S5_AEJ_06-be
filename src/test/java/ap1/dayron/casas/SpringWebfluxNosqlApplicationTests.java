package ap1.dayron.casas;

import ap1.dayron.casas.repository.EmailVerificationRepository;
import ap1.dayron.casas.repository.TranslationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SpringWebfluxNosqlApplicationTests {

	@MockBean
	private TranslationRepository translationRepository;

	@MockBean
	private EmailVerificationRepository emailVerificationRepository;

	@Test
	void contextLoads() {
	}

}
