package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.pmosystem.model.*;
import propensi.pmosystem.security.UserDetailsServiceImpl;
import propensi.pmosystem.service.*;
import org.springframework.security.core.context.SecurityContextHolder;


import java.sql.Time;
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
    private TimelineService timelineService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProjectUserService projectUserService;
    @Autowired
    private CompanyUserServiceImpl companyUserService;

    @GetMapping(value = "/add")
    private String addProjectFormPage(Model model) {
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
    ) {
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
        if (projectService.isNameUnique(project.getName(), companyName1) != true) {
            redirectAttributes.addFlashAttribute("error", String.format("Project dengan nama " + project.getName() + " dan klien " + companyName1 + " sudah terdaftar!"));
            if (accessedFrom.equals("listProject"))
                return "redirect:/project/add";
            else return "redirect:/company/project/add/" + companyId;
        }
        if (!companyName.equals("name")) {
            CompanyModel comp = projectService.checkCompanyId(companyId);
            project.setCompany(comp);
            comp.getProjectCompany().add(project);
        }
        projectService.addProject(project);
        ProjectUserModel projectUserModel = new ProjectUserModel();
        projectUserModel.setProject(project);
        projectUserModel.setUser(loginUser_);
        projectUserModel.setCreated_at(LocalDateTime.now());
        projectUserModel.setCreated_by(loginUser_.getId());
        projectUserService.addProjectUser(projectUserModel);
        model.addAttribute("project", project);
        model.addAttribute("loginUser", loginUser_);
        redirectAttributes.addFlashAttribute("success", String.format("Project dengan nama " + project.getName() + " berhasil disimpan!"));
        if (accessedFrom.equals("listProject")) {
            System.out.println("sini");
            return "redirect:/project/viewall";
        } else return "redirect:/company/view/" + companyId;
    }

    @GetMapping(value = "/viewall")
    private String listProject(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(username);
        CompanyUserModel companyUser = companyUserService.findByUser(loginUser);
        List<ProjectUserModel> projectUsers = new ArrayList<>();
//        if (loginUser.getRole().getId() == 1 || loginUser.getRole().getId() == 2)
//            projectUsers = projectUserService.findAllByUser(loginUser.getId());
        if (loginUser.getRole().getId() == 1)
            projectUsers = projectUserService.findAll();
        else if (loginUser.getRole().getId() == 2)
            projectUsers = projectUserService.findAllByUser(loginUser.getId());
        else if (loginUser.getRole().getId() == 4)
            projectUsers = projectUserService.findAllByUser(loginUser.getId());
        else if (loginUser.getRole().getId() == 3) {
            projectUsers = projectUserService.findAllByUser(loginUser.getId());
        }
        model.addAttribute("roleLogin", loginUser.getRole().getId());
        model.addAttribute("projectUsers", projectUsers);
        model.addAttribute("loginUser", loginUser);
        return "project/viewall-project";
    }

    @GetMapping("/view/{id}")
    private String detailProjectPage(@PathVariable Long id, Model model) {
        ProjectModel project = projectService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get project events
        List<EventModel> listEvent = project.getProjectEvent();

        model.addAttribute("listEvent", listEvent);
        model.addAttribute("project", project);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("roleLogin", loginUser_.getRole().getId());
        List<ProjectUserModel> listprojectusermodel = projectUserService.findAllById(id);
        if (listprojectusermodel.size() == 1 && loginUser_.getRole().getName().equals("Manajer")) {
            model.addAttribute("warning", true);
        }

        return "project/view-project";
    }

    @GetMapping("/update/{id}")
    private String updateProjectForm(@PathVariable Long id, Model model) {
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
        } else return "redirect:/access-denied";
    }

    @PostMapping("update/{id}")
    private String updateProjectSubmit(@PathVariable Long id, @ModelAttribute ProjectModel updatedProject,
                                       Model model) {
        projectService.updateProject(updatedProject);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser_);
        return "redirect:/project/view/" + id;
    }

    @GetMapping("/consultant/{id}")
    private String addConsultantForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser_);
        ProjectModel project = projectService.findById(id);
        model.addAttribute("project", project);

        List<ProjectUserModel> projectUserModelList = projectUserService.findAllById(id);
        List<UserModel> listUser = new ArrayList<UserModel>();
        for(ProjectUserModel a : projectUserModelList ){
            if(a.getUser().getRole().getName().equals("Konsultan")){
                model.addAttribute("konsultan",true);
                listUser.add(a.getUser());
            }
        }
        List<UserModel> listUserAll = userService.getUserList();
        List<UserModel> listKonsultan = new ArrayList<UserModel>();
        for(UserModel a : listUserAll) {
            if (a.getRole().getName().equals("Konsultan")) {
                listKonsultan.add(a);
            }
        }
        List<UserModel> listKonsultanfinal = new ArrayList<>(listKonsultan);
        listKonsultanfinal.removeAll(listUser);
        model.addAttribute("listKonsultan",listKonsultanfinal); // ini untuk tambah
        model.addAttribute("listUser", listUser); // ini list yang sudah ada
        model.addAttribute("jumlahKonsultan", listKonsultan.size()+1);
        return "/project/form-view-add-consultant";
    }

    @GetMapping(value = "/consultant/remove/{id}/" + "{username}")
    public ModelAndView deleteKonsultan(@PathVariable Long id,@PathVariable String username, Model model, RedirectAttributes redirectAttrs) {
        UserModel user = userService.getUserByUsername(username);
        projectUserService.removeKonsultan(id,user);
        redirectAttrs.addFlashAttribute("success",
                String.format("Konsultan dengan username "+ "`%s`" +" berhasil dihapus", username));
        return new ModelAndView("redirect:/project/consultant/{id}");
    }

    @PostMapping(value = "/consultant/add/{id}")
    private ModelAndView addKonsultanSubmit(@PathVariable Long id, @RequestParam(value = "konsultanselect", required = true) String username, @ModelAttribute UserModel user, Model model, RedirectAttributes redirectAttrs){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        UserModel loginUser_ = userService.getUserByUsername(loginUser.getUsername());
        ProjectUserModel projectUserModel = new ProjectUserModel();
        ProjectModel project = projectService.findById(id);
        UserModel usermodel = userService.getUserByUsername(username);
        projectUserModel.setProject(project);
        projectUserModel.setUser(usermodel);
        projectUserModel.setCreated_at(LocalDateTime.now());
        projectUserModel.setCreated_by(loginUser_.getId());
        projectUserService.addProjectUser(projectUserModel);
        redirectAttrs.addFlashAttribute("success", String.format("Konsultan dengan username "+ "`%s`" +" berhasil ditambahkan", username));
        return new ModelAndView("redirect:/project/consultant/{id}");
    }
    @GetMapping(value = "/timeline/{id}")
    private String timelineProjek(@PathVariable Long id,Model model) {
        ProjectModel project = projectService.findById(id);
        List<TimelineModel> timelinelist = timelineService.findAllByProjectId(id);
        model.addAttribute("timelinelist", timelinelist);
        model.addAttribute("project", project);
        return "project/timeline-project";
    }

    @GetMapping(value = "/timeline/milestone/{id}")
    private String milestoneProjek(@PathVariable Long id,Model model) {
        ProjectModel project = projectService.findById(id);
        model.addAttribute("project", project);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        UserModel loginUser_ = userService.getUserByUsername(loginUser.getUsername());
        List<TimelineModel> timelinelist = timelineService.findAllByProjectId(id);
        model.addAttribute("timelinelist", timelinelist);
        return "project/form-add-milestone";
    }

    @PostMapping(value = "/timeline/milestone/add/{id}")
    private ModelAndView milestoneProjekSubmit(@PathVariable Long id,
                                               @RequestParam(value = "milestone_name", required = true) String milestone_name,
                                               @RequestParam(value = "start_date", required = true) LocalDateTime start_date,
                                               @RequestParam(value = "end_date", required = false) LocalDateTime end_date,
                                               @RequestParam(value = "status", required = true) String status,
                                               @RequestParam(value = "weight", required = true) Integer weight,
                                               RedirectAttributes redirectAttrs,
                                               Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        UserModel loginUser_ = userService.getUserByUsername(loginUser.getUsername());
        ProjectModel project = projectService.findById(id);
        TimelineModel timelineModel = new TimelineModel();

        Integer currweight = weight + timelineService.cekCurrWeight(project.getId());
        if(currweight > 100){
            redirectAttrs.addFlashAttribute("failed", String.format("Gagal Menambahkan. Pastikan total weight tidak melebihi 100. Total weight tersisa:"+ "`%d`" +"!", 100 - currweight) );
            return new ModelAndView("redirect:/project/timeline/milestone/" + project.getId().toString());
        }
        timelineModel.setProject(project);
        timelineModel.setMilestone_name(milestone_name);
        timelineModel.setStatus(status);
        timelineModel.setWeight(weight);
        timelineModel.setStartDate(start_date);
        timelineModel.setEndDate(end_date);
        timelineModel.setCreated_at(LocalDateTime.now());
        timelineModel.setCreated_by(loginUser_.getId());
        timelineService.addTimeline(timelineModel);
        redirectAttrs.addFlashAttribute("success", String.format("Berhasil menambahkan milestone dengan nama "+ "`%s`" +"!", milestone_name));
        return new ModelAndView("redirect:/project/timeline/milestone/{id}");
    }

    @PostMapping("/timeline/milestone/update/{id}")
    private ModelAndView updateMilestoneSubmit(RedirectAttributes redirectAttrs, @PathVariable Long id, @ModelAttribute TimelineModel updateMilestone,
                                       Model model) {
        TimelineModel tl = timelineService.findById(id);
        ProjectModel project = tl.getProject();
        Integer currweight = updateMilestone.getWeight() + timelineService.cekCurrWeight(project.getId()) - tl.getWeight();
        if(currweight > 100){
            redirectAttrs.addFlashAttribute("failed", String.format("Gagal Menambahkan. Pastikan total weight tidak melebihi 100. Total weight tersisa:"+ "`%d`" +"!", 100 - timelineService.cekCurrWeight(project.getId())));
            return new ModelAndView("redirect:/project/timeline/milestone/" + project.getId().toString());
        }
        LocalDateTime created_at = tl.getCreated_at();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser_);
        updateMilestone.setProject(project);
        updateMilestone.setCreated_at(created_at);
        updateMilestone.setCreated_by(loginUser_.getId());
        timelineService.updateTimeline(updateMilestone);
        redirectAttrs.addFlashAttribute("success", String.format("Berhasil mengupdate milestone dengan nama "+ "`%s`" +"!", tl.getMilestone_name()));
        return new ModelAndView("redirect:/project/timeline/milestone/" + project.getId().toString());
    }
    @GetMapping("/timeline/milestone/remove/{id}")
    private ModelAndView removeMilestoneSubmit(RedirectAttributes redirectAttrs, @PathVariable Long id, @ModelAttribute TimelineModel updateMilestone,
                                               Model model) {
        TimelineModel tl = timelineService.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser_);
        timelineService.deleteTimeline(tl);
        redirectAttrs.addFlashAttribute("success", String.format("Berhasil menghapus milestone dengan nama "+ "`%s`" +"!", tl.getMilestone_name()));
        return new ModelAndView("redirect:/project/timeline/milestone/" + tl.getProject().getId().toString());
    }

    @GetMapping("/timeline/milestone/done/{id}")
    private ModelAndView doneMilestoneSubmit(RedirectAttributes redirectAttrs, @PathVariable Long id,
                                               Model model) {
        TimelineModel tl = timelineService.findById(id);
        tl.setStatus("1");
        timelineService.updateTimeline(tl);
        redirectAttrs.addFlashAttribute("success", String.format("Milestone dengan nama "+ "`%s`" +"telah ditandai selesai!", tl.getMilestone_name()));
        return new ModelAndView("redirect:/project/timeline/" + tl.getProject().getId().toString());
    }
}
