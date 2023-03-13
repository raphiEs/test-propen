package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import propensi.pmosystem.model.UserModel;

import java.util.List;

@Repository
public interface UserDb extends JpaRepository<UserModel, Long> {
    UserModel findByUsername(String username);
    @Query("SELECT c FROM UserModel c WHERE c.role.id = :role")
    List<UserModel> findAllByRole(@Param("role") Long role);
}
