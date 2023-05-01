package propensi.pmosystem.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.pmosystem.model.*;
import propensi.pmosystem.security.UserDetailsServiceImpl;
import propensi.pmosystem.service.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private EventService eventService;
    @Autowired
    private TimelineService timelineService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProjectUserService projectUserService;
    @Autowired
    private CompanyUserServiceImpl companyUserService;
    @Autowired
    private ResourceLoader resourceLoader;
    @GetMapping(value = "/add")
    private String addProjectFormPage(Model model) {
        ProjectModel project = new ProjectModel();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        List<CompanyModel> clients = companyService.getListCompany();
        List<CompanyModel> clientsValid = new ArrayList<>();
        for (CompanyModel companyModel: clients){
            if (companyModel.getCreated_by().equals(loginUser_.getId())){
                clientsValid.add(companyModel);
            }
        }
        if (loginUser_.getRole().getId() == 2) {
            model.addAttribute("newProject", project);
            model.addAttribute("clients", clientsValid);
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
        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        // Created by and created at attribute value
        project.setCreated_by(loginUser_.getId());
        project.setCreated_at(LocalDateTime.now());

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
        List<CompanyUserModel> companyUser = companyUserService.getCompanyUserByUserId(loginUser.getId());
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
        List <ProjectUserModel> projectUserModel = projectUserService.findAllByProjectAndRole(project.getId(), Long.valueOf(3));

        //Get project events
        List<EventModel> listEvent = project.getProjectEvent();

        model.addAttribute("listEvent", listEvent);
        model.addAttribute("project", project);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("roleLogin", loginUser_.getRole().getId());
        model.addAttribute("listConsultant", projectUserModel);
        List<ProjectUserModel> listprojectusermodel = projectUserService.findAllById(id);
        if (listprojectusermodel.size() == 1 && loginUser_.getRole().getName().equals("Manajer")) {
            model.addAttribute("warning", true);
        }
        //projek

        List<TimelineModel> timeline = timelineService.findAllByProjectId(id);
        Integer weight = 0;
        for (TimelineModel b : timeline) {
            if (b.getStatus().equals("1")) {
                weight = weight + b.getWeight();
            }
        }
        String weight_String = weight.toString();
        model.addAttribute("progress", weight_String);
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
        } else return "redirect:/project/view/"+id;
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

        if (loginUser_.getRole().getName().equals("Manajer") || loginUser_.getRole().getName().equals("Admin")) {
            ProjectModel project = projectService.findById(id);
            model.addAttribute("project", project);

            List<ProjectUserModel> projectUserModelList = projectUserService.findAllById(id);
            List<UserModel> listUser = new ArrayList<UserModel>();
            for (ProjectUserModel a : projectUserModelList) {
                if (a.getUser().getRole().getName().equals("Konsultan")) {
                    model.addAttribute("konsultan", true);
                    listUser.add(a.getUser());
                }
            }
            List<UserModel> listUserAll = userService.getUserList();
            List<UserModel> listKonsultan = new ArrayList<UserModel>();
            for (UserModel a : listUserAll) {
                if (a.getRole().getName().equals("Konsultan")) {
                    listKonsultan.add(a);
                }
            }
            List<UserModel> listKonsultanfinal = new ArrayList<>(listKonsultan);
            listKonsultanfinal.removeAll(listUser);
            model.addAttribute("listKonsultan", listKonsultanfinal); // ini untuk tambah
            model.addAttribute("listUser", listUser); // ini list yang sudah ada
            model.addAttribute("jumlahKonsultan", listUser.size() + 1);
            return "/project/form-view-add-consultant";
        }else return "redirect:/project/viewall";
    }

    @GetMapping(value = "/consultant/remove/{id}/" + "{username}")
    public ModelAndView deleteKonsultan(@PathVariable Long id,@PathVariable String username, Model model, RedirectAttributes redirectAttrs) {
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String usernameLogin = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(usernameLogin);

        if(loginUser_.getRole().getName().equals("Manajer") || loginUser_.getRole().getName().equals("Admin")) {
            UserModel user = userService.getUserByUsername(username);
            projectUserService.removeKonsultan(id, user);
            redirectAttrs.addFlashAttribute("success",
                    String.format("Konsultan dengan username " + "`%s`" + " berhasil dihapus", username));
            return new ModelAndView("redirect:/project/consultant/{id}");
        }else return new ModelAndView("redirect:/project/viewall");
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        ProjectModel project = projectService.findById(id);
        List<TimelineModel> timelinelist = timelineService.findAllByProjectId(id);
        model.addAttribute("timelinelist", timelinelist);
        model.addAttribute("project", project);
        model.addAttribute("loginUser",loginUser_);
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
        model.addAttribute("loginUser",loginUser_);
        model.addAttribute("jumlahMilestone", timelinelist.size()+1);
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
            model.addAttribute("failed", String.format("Gagal Menambahkan. Pastikan total weight tidak melebihi 100. Total weight tersisa:"+ "`%d`" +"!", 100 - currweight) );
            return new ModelAndView("redirect:/project/timeline/milestone/" + project.getId().toString());
        }
        if(end_date.isBefore(start_date)){
           redirectAttrs.addFlashAttribute("failed", String.format("Gagal Menambahkan. Pastikan tanggal mulai lebih awal daripada tanggal akhir!" ));
            model.addAttribute("failed", String.format("Gagal Menambahkan. Pastikan tanggal mulai lebih awal daripada tanggal akhir!" ));
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
        return new ModelAndView("redirect:/project/timeline/{id}");
    }

    @PostMapping("/timeline/milestone/update/{id}")
    private ModelAndView updateMilestoneSubmit(RedirectAttributes redirectAttrs, @PathVariable Long id, @ModelAttribute TimelineModel updateMilestone,
                                       Model model) {
        TimelineModel tl = timelineService.findById(id);
        ProjectModel project = tl.getProject();
        Integer currweight = updateMilestone.getWeight() + timelineService.cekCurrWeight(project.getId()) - tl.getWeight();
        if(currweight > 100){
            redirectAttrs.addFlashAttribute("error", String.format("Gagal Menambahkan. Pastikan total weight tidak melebihi 100. Total weight tersisa:"+ "`%d`" +"!", 100 - timelineService.cekCurrWeight(project.getId())));
            return new ModelAndView("redirect:/project/timeline/milestone/" + project.getId().toString());
        }
        LocalDateTime created_at = tl.getCreated_at();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser_);
        if(updateMilestone.getEndDate().isBefore(updateMilestone.getStartDate())){
            redirectAttrs.addFlashAttribute("failed", String.format("Gagal Menambahkan. Pastikan tanggal mulai lebih awal daripada tanggal akhir!" ));
            model.addAttribute("failed", String.format("Gagal Menambahkan. Pastikan tanggal mulai lebih awal daripada tanggal akhir!" ));

            return new ModelAndView("redirect:/project/timeline/milestone/" + project.getId().toString());
        }
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
        return new ModelAndView("redirect:/project/timeline/milestone/" + tl.getProject().getId().toString());
    }

    @GetMapping(value = "/events/{projectId}/summary.pdf", produces = "application/pdf")
    @ResponseBody
    public byte[] generateEventSummary(@PathVariable("projectId") long projectId) throws Exception {

        // Get events from the database using the projectId
        List<EventModel> events = eventService.getListEventByProjectId(projectId);
        // Create the PDF document
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte canvas = writer.getDirectContentUnder();
//        Image image = Image.getInstance("../resources/static/images/bg-image.png");
        Resource imageResource = resourceLoader.getResource("classpath:/static/images/bg-image.png");
        String imageUrl = imageResource.getURL().toString();
        Image image = Image.getInstance(new URL(imageUrl));
        image.scaleAbsolute(document.getPageSize());
        image.setAbsolutePosition(0, 0);
        canvas.addImage(image);
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setTextMatrix(document.right()-85, document.bottom()-10);
        canvas.showText("Generated at " + LocalDate.now());
        canvas.endText();

        Paragraph title = new Paragraph("Project: " + projectService.findById(projectId).getName(), new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD));
        Paragraph client = new Paragraph( projectService.findById(projectId).getCompany().getName(), new Font(Font.FontFamily.TIMES_ROMAN, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(150);
        client.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(client);
        document.add(new Paragraph("\n"));
        int count = 1;
        // Add event information to the PDF
        for (EventModel event : events) {
            document.add(new Paragraph("Event #" + count++,new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.BOLD)));
            document.add(new Paragraph("Name: " + event.getName()));
            document.add(new Paragraph("Start Date: " + event.getStartDate().toString().substring(0, 10)));
            // document.add(new Paragraph("End Date: " + event.getEndDate().toString().substring(0, 10)));
            document.add(new Paragraph("Summary: " + event.getSummary()));
            document.add(new Paragraph("Detailed Summary: " + event.getDetailedSummary()));
            document.add(new Paragraph("\n"));
        }

        document.close();
        // Return the PDF as a byte array
        return baos.toByteArray();
    }
}
