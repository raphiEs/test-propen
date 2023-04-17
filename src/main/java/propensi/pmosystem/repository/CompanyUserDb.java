package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.service.CompanyUserService;
import propensi.pmosystem.service.CompanyUserServiceImpl;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyUserDb extends JpaRepository<CompanyUserModel, Long> {
    List<CompanyUserModel> findAll();
    CompanyUserModel findByUser(UserModel user);

    @Query("SELECT c FROM CompanyUserModel c WHERE c.user.id = :userId")
    List<CompanyUserModel> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM CompanyUserModel c WHERE c.company.id = :companyId")
    List<CompanyUserModel> findByCompanyId(@Param("companyId") Long companyId);

}
