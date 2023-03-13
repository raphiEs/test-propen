package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
        Integer role = 4;
        List<UserModel> clients = userService.getUserByRole(role.longValue());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("project", project);
        model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        return "project/form-add-project";
    }
    @PostMapping(value = "/add")
    private String addProjectSubmit(@ModelAttribute ProjectModel project, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        projectService.addProject(project);
        model.addAttribute("user", project);
        model.addAttribute("loginUser_", loginUser);
        return "redirect:/project/viewall";
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
        model.addAttribute("loginUser", loginUser);
        return "project/viewall-project";
    }
    @GetMapping("/view/{id}")
    private String detailProjectPage(@PathVariable Long id, Model model){
        ProjectModel project = projectService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("project", project);
        model.addAttribute("loginUser", loginUser_);
        return "project/view-project";
    }
    @GetMapping("/update/{id}")
    private String updateProjectForm(@PathVariable Long id, Model model){
        ProjectModel oldProject = projectService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("oldProject", oldProject);
        model.addAttribute("loginUser", loginUser_);
        return "project/form-update-project";
    }
    @PostMapping("update/{id}")
    private String updateProjectSubmit(@PathVariable Long id, @ModelAttribute ProjectModel updatedProject,
                                       Model model){
        projectService.updateProject(updatedProject);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser_);
        return "redirect:/project/view/" + id;
    }

}
