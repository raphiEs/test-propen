package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propensi.pmosystem.model.TimelineModel;

import java.util.List;
import java.util.Optional;

public interface TimelineDb extends JpaRepository<TimelineModel, String> {
    List<TimelineModel> findAllByProjectId(Long id);
    Optional<TimelineModel> findById(Long id);
}

