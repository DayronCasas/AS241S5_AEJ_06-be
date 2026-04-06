package ap1.dayron.casas.repository;

import ap1.dayron.casas.model.SongRecognition;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SongRecognitionRepository extends ReactiveMongoRepository<SongRecognition, String> {

    Flux<SongRecognition> findAllByOrderByRecognizedAtDesc();

    Flux<SongRecognition> findByArtistIgnoreCase(String artist);
}
