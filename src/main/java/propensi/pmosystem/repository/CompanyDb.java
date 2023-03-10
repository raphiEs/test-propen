package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.pmosystem.model.CompanyModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyDb extends JpaRepository<CompanyModel, Long> {
    Optional<CompanyModel> findById(Long id);

    List<CompanyModel> findAll();
}
