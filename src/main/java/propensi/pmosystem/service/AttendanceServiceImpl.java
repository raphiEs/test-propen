package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.AttendanceModel;
import propensi.pmosystem.model.EventModel;
import propensi.pmosystem.repository.AttendanceDb;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService{
    @Autowired
    AttendanceDb attendanceDb;

    @Override
    public AttendanceModel findById(Long idAttendance) {
        Optional<AttendanceModel> attendance = attendanceDb.findById(idAttendance);
        if (attendance.isPresent())
            return attendance.get();
        return null;
    }

    @Override
    public List<AttendanceModel> findAll() {
        return attendanceDb.findAll();
    }

    @Override
    public List<AttendanceModel> findEventAttendance(EventModel event) {
        return attendanceDb.findAllByEvent(event);
    }

    @Override
    public AttendanceModel addAttendance(AttendanceModel newAttendance) {
        return attendanceDb.save(newAttendance);
    }

    @Override
    public void delete(AttendanceModel attendance) {
        attendanceDb.delete(attendance);
    }
}
