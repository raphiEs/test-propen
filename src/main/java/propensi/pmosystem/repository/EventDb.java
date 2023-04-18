package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.EventModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventDb extends JpaRepository<EventModel, Long> {
    Optional<EventModel> findById(Long id);
    List<EventModel>  findByProjectId(Long projectId);
    List<EventModel> findAll();
}