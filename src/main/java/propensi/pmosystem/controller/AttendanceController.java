package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.pmosystem.model.AttendanceModel;
import propensi.pmosystem.model.EventModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.service.AttendanceService;
import propensi.pmosystem.service.EventService;
import propensi.pmosystem.service.ProjectService;
import propensi.pmosystem.service.UserService;

import java.time.LocalDateTime;

@Controller
@RequestMapping(value = "/attendance")
public class AttendanceController {
    @Autowired
    AttendanceService attendanceService;
    @Autowired
    UserService userService;
    @Autowired
    EventService eventService;

    @Autowired
    ProjectService projectService;

    @GetMapping(value = "/{idEvent}")
    public String addAttendanceFormPage(Model model, @PathVariable Long idEvent){
        AttendanceModel newAttendance = new AttendanceModel();
        EventModel event = eventService.getEventById(idEvent);
        ProjectModel project = projectService.findById(event.getProject().getId());
        model.addAttribute("event", event);
        model.addAttribute("project", project);
        model.addAttribute("attendance", newAttendance);

        return "attendance/form-add";
    }

    @PostMapping(value = "/{idEvent}")
    public String addAttendanceSubmit(Model model, @ModelAttribute AttendanceModel attendance,
                                      @PathVariable Long idEvent, RedirectAttributes redirectAttributes){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Set attendance attrs
        attendance.setCreated_at(LocalDateTime.now());
        attendance.setCreated_by(loginUser_.getId());
        EventModel event = eventService.getEventById(idEvent);
        attendance.setEvent(event);

        //Add attendance to DB
        attendanceService.addAttendance(attendance);

        //Success pop-up message
        redirectAttributes.addFlashAttribute("success",
                String.format("Partisipan '" + attendance.getName() + "' berhasil ditambahkan pada event "+ event));
        System.out.println(attendance.getName());
        model.addAttribute("participant",attendance);
        return "attendance/review-absensi";
    }

    @GetMapping(value = "/{idEvent}/delete/"+"{id}")
    public ModelAndView deleteParticipant(Model model, @PathVariable Long id, @PathVariable Long idEvent,
                                          RedirectAttributes redirectAttributes){
        //Deleting attendance from DB
        AttendanceModel participant = attendanceService.findById(id);
        EventModel event = eventService.getEventById(idEvent);
        event.getEventAttendance().remove(participant);

        //Success pop-up message
        redirectAttributes.addFlashAttribute("success",
                String.format("Partisipan '" + participant.getName() + "' berhasil dihapus dari event "+ event));
        model.addAttribute("participant", participant);
        return new ModelAndView("redirect:/attendance/{idEvent}");
    }
}
