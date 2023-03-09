package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.BusinessModel;
import propensi.pmosystem.model.CompanyModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessDb extends JpaRepository<BusinessModel, Long> {
    Optional<BusinessModel> findById(Long id);

    List<BusinessModel> findAll();
}
