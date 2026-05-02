package ap1.dayron.casas.repository;

import ap1.dayron.casas.model.Translation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TranslationRepository extends ReactiveMongoRepository<Translation, String> {

    Flux<Translation> findByDeletedFalseOrderByTranslatedAtDesc();
}
