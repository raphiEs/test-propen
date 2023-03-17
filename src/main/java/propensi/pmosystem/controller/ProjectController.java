package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.security.UserDetailsServiceImpl;
import propensi.pmosystem.service.*;
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
    private CompanyService companyService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private CompanyUserServiceImpl companyUserService;

    @GetMapping(value = "/add")
    private String addProjectFormPage(Model model){
        ProjectModel project = new ProjectModel();
        Integer role = 4;
        List<CompanyModel> clients = companyService.getListCompany();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("newProject", project);
        model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("accessedFrom", "listProject");
        return "project/form-add-project";
    }
    @PostMapping(value = "/add")
    private String addProjectSubmit(@ModelAttribute ProjectModel project, Model model,
                                    @RequestParam(value = "accessedFrom", required = false) String accessedFrom,
                                    @RequestParam(value = "companyName", required = false) String companyName,
                                    @RequestParam(value = "companyId", required = false) String companyId
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        System.out.println(companyName);
        System.out.println(companyId);
        System.out.println(accessedFrom);
        if (companyName == "name" || companyName == null){
            CompanyModel comp = projectService.checkCompanyId(companyName);
            project.setCompany(comp);
            comp.getProjectCompany().add(project);
        }
        projectService.addProject(project);
        model.addAttribute("project", project);
        model.addAttribute("loginUser", loginUser_);
        if (accessedFrom.equals("listProject")){
            System.out.println("sini");
            return "redirect:/project/viewall";}
        else return "redirect:/company/view/" + companyId;
    }
    @GetMapping(value = "/viewall")
    private String listProject(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(username);
        CompanyUserModel companyUser = companyUserService.findByUser(loginUser);
        List<ProjectModel> projects = new ArrayList<>();
        if (loginUser.getRole().getId() == 1 || loginUser.getRole().getId() == 2)
            projects = projectService.findAll();
        else if (loginUser.getRole().getId()==4)
            projects = projectService.findAllByClient(companyUser.getCompany().getId());
        else if (loginUser.getRole().getId()==3) {
            projects = projectService.findAllByConsultant(loginUser.getId());
        }        System.out.print(projects.size());
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
        List<CompanyModel> clients = companyService.getListCompany();
        model.addAttribute("clients", clients);
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
