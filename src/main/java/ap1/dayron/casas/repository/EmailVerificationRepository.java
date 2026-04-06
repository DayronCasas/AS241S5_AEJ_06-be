package ap1.dayron.casas.repository;

import ap1.dayron.casas.model.EmailVerification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EmailVerificationRepository extends ReactiveMongoRepository<EmailVerification, String> {

    Flux<EmailVerification> findByEmail(String email);

    Flux<EmailVerification> findByValid(boolean valid);

    Flux<EmailVerification> findAllByOrderByVerifiedAtDesc();
}
