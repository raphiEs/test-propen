package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.service.CompanyUserService;

import java.util.List;
@Repository
public interface CompanyUserDb extends JpaRepository<CompanyUserModel, Long> {
    List<CompanyUserModel> findAll();
    CompanyUserModel findByUser(UserModel user);
}
