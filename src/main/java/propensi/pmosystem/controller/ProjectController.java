package propensi.pmosystem.controller;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.security.UserDetailsServiceImpl;
import propensi.pmosystem.service.CompanyUserServiceImpl;
import propensi.pmosystem.service.ProjectService;
import propensi.pmosystem.service.RoleService;
import propensi.pmosystem.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private CompanyUserServiceImpl companyUserService;

    @GetMapping(value = "/add")
    private String addProjectFormPage(Model model){
        ProjectModel project = new ProjectModel();
        List<UserModel> clients = userService.getUserByRole(Integer.toUnsignedLong(4));
        model.addAttribute("project", project);
        model.addAttribute("clients", clients);
        return "form-add-project";
    }
    @PostMapping(value = "/add")
    private String addProjectSubmit(@ModelAttribute ProjectModel project, Model model){
        projectService.addProject(project);
        model.addAttribute("user", project);
        return "redirect:/";
    }
    @GetMapping(value = "/viewall")
    private String listProject(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(username);
        CompanyUserModel companyUser = companyUserService.findByUser(loginUser);
        List<ProjectModel> projects = new ArrayList<>();
        if (loginUser.getRole().equals(Integer.toUnsignedLong(2)))
            projects = projectService.findAll();
        else if (loginUser.getRole().equals(Integer.toUnsignedLong(4)))
            projects = projectService.findAllByClient(companyUser.getCompany().getId());
        else if (loginUser.getRole().equals(Integer.toUnsignedLong(3))) {
            projects = projectService.findAllByConsultant(loginUser.getId());
        }
        model.addAttribute("projects", projects);
        return "viewall-project";
    }
    @GetMapping("/view/{id}")
    public String detailProjectPage(@PathVariable Long id, Model model){
        ProjectModel project = projectService.findById(id);
        model.addAttribute("project", project);
        return "view-project";
    }
}
