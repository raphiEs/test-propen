package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.UserModel;

import java.util.List;

public interface ProjectDb extends JpaRepository<ProjectModel, Long>{
    List<ProjectModel> findAll();
    @Query("SELECT c FROM ProjectModel c WHERE c.company.id = :klien")
    List<ProjectModel> findAllByClient(@Param("klien") Long company);
    @Query("SELECT c FROM ProjectModel c INNER JOIN ProjectUserModel d " +
            "ON c.id = d.project.id WHERE d.user.id = :konsultan")
    List<ProjectModel> findAllByConsultant(@Param("konsultan") Long consultant);
}
