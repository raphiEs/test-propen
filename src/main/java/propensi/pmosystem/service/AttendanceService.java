package propensi.pmosystem.service;

import propensi.pmosystem.model.AttendanceModel;
import propensi.pmosystem.model.EventModel;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    AttendanceModel findById(Long idAttendance);
    List<AttendanceModel> findAll();
    List<AttendanceModel> findEventAttendance(EventModel event);

    AttendanceModel addAttendance(AttendanceModel newAttendance);

    void delete(AttendanceModel attendance);
}
