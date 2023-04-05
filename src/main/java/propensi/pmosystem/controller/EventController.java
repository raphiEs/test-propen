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

        //Set event attrs
        event.setProject(projectService.findById(Long.parseLong(idProyek)));
        event.setCreated_at(LocalDateTime.now());
        event.setCreated_by(loginUser_.getId());
        event.setMomName("-");
        event.setMomUrl("-");
        event.setSummary("-");
        event.setDetailedSummary("-");

        System.out.println("Has set attribute but has yet to save to db");

        //Add Event to db
        eventService.addEvent(event);
        System.out.println("Saved to db");

        //Success pop-up message
        //redirectAttributes.addFlashAttribute("success",
        //      String.format("Klien '" + company.getName() + "' berhasil ditambahkan"));


        model.addAttribute("loginUser", loginUser_);
        return "redirect:/company/view/all";
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

        //Get project by id
        ProjectModel project = projectService.findById(event.getProject().getId());

        //model.addAttribute("message", message);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("project", project);
        model.addAttribute("event", event);
        return "event/view-event";
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

        //model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("event", event);
        return "event/form-add-mom-event";
    }

    @PostMapping("/event/mom/update")
    public String updateMoMSubmitPage(@ModelAttribute EventModel updatedEvent,
                                      Model model){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Update event
        eventService.updateEvent(updatedEvent);

        model.addAttribute("loginUser", loginUser_);
        return "event/view-event";
    }
}
