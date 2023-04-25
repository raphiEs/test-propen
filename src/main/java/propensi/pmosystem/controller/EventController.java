package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.pmosystem.model.*;
import propensi.pmosystem.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EventController {

    @Qualifier("companyServiceImpl")
    @Autowired
    private CompanyService companyService;

    @Qualifier("companyUserServiceImpl")
    @Autowired
    private CompanyUserService companyUserService;

    @Qualifier("businessServiceImpl")
    @Autowired
    private BusinessService businessService;

    @Qualifier("projectServiceImpl")
    @Autowired
    private ProjectService projectService;

    @Qualifier("eventServiceImpl")
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectUserService projectUserService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("project/view/{id}/event/add")
    private String addEventFormPage(@PathVariable Long id,
                                    Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Create new empty event
        EventModel event = new EventModel();
        //List<UserModel> listConsultant = userService.getUserByRole(Long.parseLong("4"));

        //Get belonging project
        ProjectModel project = projectService.findById(id);
        String namaProyek = project.getName();

        //Set event attr
        event.setProject(project);

        model.addAttribute("event", event);
        model.addAttribute("namaProyek", namaProyek);
        //model.addAttribute("listConsultant", listConsultant);
        model.addAttribute("loginUser", loginUser_);

        return "event/form-add-event";
    }

    @PostMapping("event/add")
    private String addEventSubmit(@ModelAttribute EventModel event,
                                    @RequestParam String idProyek,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        /*
        //Project visibility auth
        List<ProjectUserModel> projectUsers = new ArrayList<>();
        if (loginUser_.getRole().getId() == 1)
            projectUsers = projectUserService.findAll();
        else if (loginUser_.getRole().getId() == 2)
            projectUsers = projectUserService.findAllByUser(loginUser_.getId());
        else if (loginUser_.getRole().getId() == 4)
            projectUsers = projectUserService.findAllByUser(loginUser_.getId());
        else if (loginUser_.getRole().getId() == 3) {
            projectUsers = projectUserService.findAllByUser(loginUser_.getId());
        }
        */
        //Set event attrs
        event.setProject(projectService.findById(Long.parseLong(idProyek)));
        event.setCreated_at(LocalDateTime.now());
        event.setCreated_by(loginUser_.getId());
        event.setMomName("-");
        event.setMomUrl("-");
        event.setSummary("-");
        event.setDetailedSummary("-");

        // Add event to project
        ProjectModel project = projectService.findById(Long.parseLong(idProyek));
        project.getProjectEvent().add(event);

        //Add Event to db
        eventService.addEvent(event);
        System.out.println("Saved to db");

        //model.addAttribute("roleLogin", loginUser_.getRole().getId());
        //model.addAttribute("projectUsers", projectUsers);

        //Success pop-up message
        redirectAttributes.addFlashAttribute("success",
                String.format("Event '" + event.getName() + "' berhasil ditambahkan"));

        model.addAttribute("loginUser", loginUser_);
        return "redirect:/project/view/" + idProyek;
    }

    @GetMapping("/event/view/{id}")
    public String viewDetailEvent(@PathVariable Long id,
                                    Model model){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get event by id
        EventModel event = eventService.getEventById(id);
        //System.out.println("Event id: "+ event.getId());

        //Get project of event
        ProjectModel project = projectService.findById(event.getProject().getId());

        //Get attendance list of event
        List<AttendanceModel> listAttendance = attendanceService.findEventAttendance(event);
        System.out.println(listAttendance.size());
        //model.addAttribute("message", message);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("project", project);
        model.addAttribute("event", event);
        model.addAttribute("listAttendance", listAttendance);
        return "event/view-event";
    }

    @GetMapping("/event/update/{id}")
    public String updateEventFormPage(@PathVariable Long id,
                                    Model model){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get event to update
        EventModel event = eventService.getEventById(id);

        //Get project of event
        ProjectModel project = projectService.findById(event.getProject().getId());

        //model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("project", project);
        model.addAttribute("event", event);
        return "event/form-update-event";
    }

    @PostMapping("/event/update")
    public String updateEventSubmitPage(@ModelAttribute EventModel updatedEvent,
                                        @RequestParam String projectId,
                                        Model model,
                                        RedirectAttributes redirectAttributes){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Set attrs
        ProjectModel assignedProject = projectService.findById(Long.parseLong(projectId));
        updatedEvent.setProject(assignedProject);

        //Update event
        eventService.updateEvent(updatedEvent);

        redirectAttributes.addFlashAttribute("success",
                String.format("Informasi event '" + updatedEvent.getName() + "' berhasil diubah"));

        model.addAttribute("project", assignedProject);
        model.addAttribute("event", updatedEvent);
        model.addAttribute("loginUser", loginUser_);
        return "redirect:/event/view/" + updatedEvent.getId().toString();
    }

    @GetMapping("/event/mom/update/{id}")
    public String updateMoMFormPage(@PathVariable Long id,
                                        Model model){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get event to update
        EventModel event = eventService.getEventById(id);

        //Get project of event
        ProjectModel project = projectService.findById(event.getProject().getId());

        //model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("project", project);
        model.addAttribute("event", event);
        return "event/form-update-mom-event";
    }

    @PostMapping("/event/mom/update")
    public String updateMoMSubmitPage(@ModelAttribute EventModel updatedEvent,
                                      @RequestParam String projectId,
                                      Model model,
                                      RedirectAttributes redirectAttributes){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Set attrs
        ProjectModel assignedProject = projectService.findById(Long.parseLong(projectId));
        updatedEvent.setProject(assignedProject);
        updatedEvent.setMomLastUpdate(LocalDateTime.now());

        //Update event
        eventService.updateEvent(updatedEvent);

        redirectAttributes.addFlashAttribute("success",
                String.format("Link MoM event '" + updatedEvent.getName() + "' berhasil diubah"));

        model.addAttribute("project", assignedProject);
        model.addAttribute("event", updatedEvent);
        model.addAttribute("loginUser", loginUser_);
        return "redirect:/event/view/" + updatedEvent.getId().toString();
    }

    @GetMapping(value = "/event/remove/{id}")
    public String deleteUserForm(@PathVariable Long id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get event to delete
        EventModel event = eventService.getEventById(id);
        eventService.deleteEvent(event);

        //Get new list
        ProjectModel project = event.getProject();
        List<EventModel> listEvent = project.getProjectEvent();

        redirectAttributes.addFlashAttribute("success",
                String.format("Event '" + event.getName() + "' berhasil dihapus"));

        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("listEvent", listEvent);

        return "redirect:/project/view/" + project.getId().toString();
    }

<<<<<<< HEAD
<<<<<<< HEAD

=======
=======
>>>>>>> f14e324b9cd47d9ef96b7b4d2a5c00a14daec2d5
    @GetMapping(value = "event/view/attendance/{idEvent}")
    public String addAttendanceFormPage(Model model, @PathVariable Long idEvent){
        AttendanceModel newAttendance = new AttendanceModel();
        EventModel event = eventService.getEventById(idEvent);
        ProjectModel project = projectService.findById(event.getProject().getId());
        model.addAttribute("event", event);
        model.addAttribute("project", project);
        model.addAttribute("accessedFrom","detailEvent");
        model.addAttribute("attendance", newAttendance);

        return "attendance/form-add";
    }
<<<<<<< HEAD
>>>>>>> 1bfc153e895ea5b7164e76155a7d574eeb6be6f6
=======
>>>>>>> f14e324b9cd47d9ef96b7b4d2a5c00a14daec2d5
}
