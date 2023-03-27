package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.ProjectUserModel;

import java.util.List;

@Repository
public interface ProjectUserDb extends JpaRepository<ProjectUserModel, Long> {
    List<ProjectUserModel> findAllByProjectId(Long id);
    List<ProjectUserModel> findAllByUserId(Long id);
}