package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.pmosystem.model.*;
import propensi.pmosystem.security.UserDetailsServiceImpl;
import propensi.pmosystem.service.*;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDateTime;
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
    private ProjectUserService projectUserService;
    @Autowired
    private CompanyUserServiceImpl companyUserService;

    @GetMapping(value = "/add")
    private String addProjectFormPage(Model model){
        ProjectModel project = new ProjectModel();
        List<CompanyModel> clients = companyService.getListCompany();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        if (loginUser_.getRole().getId() == 2) {
            model.addAttribute("newProject", project);
            model.addAttribute("clients", clients);
            model.addAttribute("loginUser", loginUser_);
            model.addAttribute("accessedFrom", "listProject");
            return "project/form-add-project";
        } else return "redirect:/access-denied";
    }
    @PostMapping(value = "/add")
    private String addProjectSubmit(@ModelAttribute ProjectModel project, Model model,
                                    @RequestParam(value = "accessedFrom", required = false) String accessedFrom,
                                    @RequestParam(value = "companyName", required = false) String companyName,
                                    @RequestParam(value = "companyId", required = false) Long companyId,
                                    RedirectAttributes redirectAttributes
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        System.out.println(companyName);
        System.out.println(companyId);
        System.out.println(accessedFrom);
        String companyName1 = "";
        if (accessedFrom.equals("listProject"))
            companyName1 = project.getCompany().getName();
        else companyName1 = companyName;
        System.out.println(project.getName());
        System.out.println(projectService.isNameUnique(project.getName(), companyName1) != true);
        if (projectService.isNameUnique(project.getName(), companyName1) != true){
            redirectAttributes.addFlashAttribute("error", String.format("Project dengan nama "+project.getName()+" dan klien "+companyName1+" sudah terdaftar!"));
            if (accessedFrom.equals("listProject"))
                return "redirect:/project/add";
            else return "redirect:/company/project/add/"+companyId;
        }
        if (!companyName.equals("name")){
            CompanyModel comp = projectService.checkCompanyId(companyId);
            project.setCompany(comp);
            comp.getProjectCompany().add(project);
        }
        projectService.addProject(project);
        projectUserService.addProjectUser(new ProjectUserModel(1,loginUser_.getRole().getId(), LocalDateTime.now(), project, loginUser_));
        model.addAttribute("project", project);
        model.addAttribute("loginUser", loginUser_);
        redirectAttributes.addFlashAttribute("success", String.format("Project dengan nama "+project.getName()+ " berhasil disimpan!"));
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
        List<ProjectUserModel> projectUsers = new ArrayList<>();
        if (loginUser.getRole().getId() == 1 || loginUser.getRole().getId() == 2)
            projectUsers = projectUserService.findAllByUser(loginUser.getId());
        else if (loginUser.getRole().getId()==4)
            projectUsers = projectUserService.findAllByUser(loginUser.getId());
        else if (loginUser.getRole().getId()==3) {
            projectUsers = projectUserService.findAllByUser(loginUser.getId());
        }
        model.addAttribute("roleLogin", loginUser.getRole().getId());
        model.addAttribute("projectUsers", projectUsers);
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
        model.addAttribute("roleLogin",loginUser_.getRole().getId());
        return "project/view-project";
    }
    @GetMapping("/update/{id}")
    private String updateProjectForm(@PathVariable Long id, Model model){
        ProjectModel oldProject = projectService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        if (loginUser_.getRole().getId() == 2) {
            List<CompanyModel> clients = companyService.getListCompany();
            model.addAttribute("clients", clients);
            model.addAttribute("oldProject", oldProject);
            model.addAttribute("loginUser", loginUser_);
            return "project/form-update-project";
        }else return "redirect:/access-denied";
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
