package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.ProjectUserModel;
import propensi.pmosystem.model.UserModel;

import java.util.List;

@Repository
public interface ProjectUserDb extends JpaRepository<ProjectUserModel, Long> {
    @Query("SELECT a FROM ProjectUserModel a WHERE a.project.id = :project and a.user.role.id = :role")
    List<ProjectUserModel> findAllByProjectIdAndUserRole(@Param("project") Long id, @Param("userRole") Long role);
    List<ProjectUserModel> findAllByProjectId(Long id);
    List<ProjectUserModel> findAllByUserId(Long id);
}