package propensi.pmosystem.service;

import propensi.pmosystem.model.AttendanceModel;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    AttendanceModel findById(Long idAttendance);
    List<AttendanceModel> findAll();
    List<AttendanceModel> findAllByEvent(Long idEvent);

    AttendanceModel addAttendance(AttendanceModel newAttendance);
}
