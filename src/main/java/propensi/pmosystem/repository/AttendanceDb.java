package propensi.pmosystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.pmosystem.model.AttendanceModel;
import propensi.pmosystem.model.EventModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceDb extends JpaRepository<AttendanceModel, String> {
    List<AttendanceModel> findAll();
    List<AttendanceModel> findAllByEvent(EventModel event);
    Optional<AttendanceModel> findById(Long id);
}
