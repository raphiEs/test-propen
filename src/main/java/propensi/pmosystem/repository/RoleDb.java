package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.pmosystem.model.RoleModel;

@Repository
public interface RoleDb extends JpaRepository<RoleModel,Long> {
}
