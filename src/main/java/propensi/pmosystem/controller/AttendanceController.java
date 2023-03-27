package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import propensi.pmosystem.model.AttendanceModel;
import propensi.pmosystem.service.AttendanceService;

@Controller
public class AttendanceController {
    @Autowired
    AttendanceService attendanceService;

    @GetMapping(value = "/attendance/{idEvent}")
    public String addAttendanceFormPage(Model model){
        AttendanceModel newAttendance = new AttendanceModel();
        model.addAttribute("attendance",newAttendance);

        return "attendance/form-add";
    }
}
