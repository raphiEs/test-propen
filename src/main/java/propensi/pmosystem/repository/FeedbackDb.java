package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.FeedbackModel;
import propensi.pmosystem.model.ProjectModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackDb extends JpaRepository<FeedbackModel, Long> {
    List<FeedbackModel> findAll();
    Optional<FeedbackModel> findById(Long id);

}
