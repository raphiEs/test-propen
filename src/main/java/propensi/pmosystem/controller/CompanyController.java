package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.pmosystem.model.*;
import propensi.pmosystem.repository.BusinessDb;
import propensi.pmosystem.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CompanyController {

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

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectUserService projectUserService;

    @GetMapping("/company/add")
    private String addCompanyFormPage(Model model) {
        //Auth
        //Integer role = 1;
        //List<UserModel> clients = userService.getUserByRole(role.longValue());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Create new empty company
        CompanyModel company = new CompanyModel();
        company.initializeListProject();
        List<BusinessModel> listBusiness = businessService.getListBusiness();

        model.addAttribute("company", company);
        model.addAttribute("listBusiness", listBusiness);
        model.addAttribute("loginUser", loginUser_);
        //log.info("Manajer memulai proses 'tambah perusahaan'");
        return "klien/form-add-klien";
    }

    @PostMapping("/company/add")
    private String addCompanySubmit(@ModelAttribute CompanyModel company,
                                    @RequestParam String businessId,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Set company attrs
        company.setCreated_at(LocalDateTime.now());
        company.setCreated_by(loginUser_.getId());
        if (businessId != ""){
            company.setBusiness(businessService.getBusinessById(Long.parseLong(businessId)));
        } else {
            company.setBusiness(null);
        }

        //Add Company to db
        companyService.addCompany(company);

        //Add CompanyUser to db
        CompanyUserModel companyUser = new CompanyUserModel();
        companyUser.setCompany(company);
        companyUser.setUser(loginUser_);
        companyUser.setCreated_at(LocalDateTime.now());
        companyUser.setCreated_by(loginUser_.getId());
        companyUserService.addCompanyUser(companyUser);

        //Success pop-up message
        redirectAttributes.addFlashAttribute("success",
                String.format("Klien '" + company.getName() + "' berhasil ditambahkan"));

        
        model.addAttribute("loginUser", loginUser_);
        return "redirect:/company/view/all";
    }

    @GetMapping("/company/update/{id}")
    public String updateCompanyFormPage(@PathVariable Long id,
                                        Model model){
        //Auth
        //Integer role = 1;
        //List<UserModel> clients = userService.getUserByRole(role.longValue());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get company to update
        CompanyModel company = companyService.getCompanyById(id);
        List<BusinessModel> listBusiness = businessService.getListBusiness();
        BusinessModel companyBusiness = company.getBusiness();

        //model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("company", company);
        model.addAttribute("listBusiness", listBusiness);
        model.addAttribute("companyBusiness", companyBusiness);
        return "klien/form-update-klien";
    }

    @PostMapping("/company/update")
    public String updateCompanySubmitPage(@ModelAttribute CompanyModel updatedCompany,
                                          @RequestParam String businessId,
                                          Model model, RedirectAttributes redirectAttributes){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        updatedCompany.setBusiness(businessService.getBusinessById(Long.parseLong(businessId)));
//        companyService.updateCompany(updatedCompany);
//        String message = "Klien dengan nama '" + updatedCompany.getName() + "' berhasil diperbarui";
//
//        model.addAttribute("loginUser", loginUser_);
//        model.addAttribute("message", message);
//        return "form-success";

        companyService.updateCompany(updatedCompany);
        redirectAttributes.addFlashAttribute("success",
                String.format("Klien '" + updatedCompany.getName() + "' berhasil diubah"));
        model.addAttribute("loginUser", loginUser_);
        return "redirect:/company/view/" + updatedCompany.getId().toString();
    }

    @GetMapping("/company/view/all")
    public String viewAllCompany(Model model){
        //Auth
        //Integer role = 1;
        //List<UserModel> clients = userService.getUserByRole(role.longValue());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get company for admin (all company)
        List<CompanyModel> listCompany = new ArrayList<>();
        if (loginUser_.getRole().getId() == 1){
            listCompany.addAll(companyService.getListCompany());
        }
        //Get company for manager
        else if (loginUser_.getRole().getId() == 2){
            List<CompanyUserModel> listCompanyUser = companyUserService.getCompanyUserByUserId(loginUser_.getId());
            //Iterate company created by manager using table "company_user"
            for (int i = 0; i<listCompanyUser.size(); i++){
                CompanyModel tempCompany = companyService.getCompanyById(listCompanyUser.get(i).getCompany().getId());
                listCompany.add(tempCompany);
            }
        }

        //model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("listCompany", listCompany);
        return "klien/view-all-klien";
    }

    @GetMapping("/company/view/{id}")
    public String viewDetailCompany(@PathVariable Long id,
                                    Model model){
        //Auth
        Integer role = 1;
        List<UserModel> clients = userService.getUserByRole(role.longValue());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get company by id
        CompanyModel company = companyService.getCompanyById(id);
        List<ProjectModel> listProject = company.getProjectCompany();
        //String message = listProject.get(0).getName();

        //model.addAttribute("message", message);
        model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("company", company);
        model.addAttribute("listProject", listProject);
        return "klien/view-detail-klien";
    }

    @GetMapping("/company/project/add/{id}")
    public String addCompanyProjectForm(@PathVariable Long id,
                                        Model model) {
        //Auth
        Integer role = 1;
        List<UserModel> clients = userService.getUserByRole(role.longValue());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        //Get company by id & company's projects
        CompanyModel company = companyService.getCompanyById(id);
        List<ProjectModel> listProject = projectService.findAll();

        model.addAttribute("clients", clients);
        model.addAttribute("loginUser", loginUser_);
        model.addAttribute("company", company);
        model.addAttribute("listProject", listProject);
        model.addAttribute("accessedFrom", "detailKlien");
        return "/project/form-add-project";
    }

    @GetMapping("/company/business/add")
    public String addCompanyBusinessForm(Model model){
        BusinessModel business = new BusinessModel();

        model.addAttribute("business", business);
        return "";
    }
    @PostMapping("/company/business/add")
    public String addCompanyBusinessSubmit(@ModelAttribute BusinessModel business, RedirectAttributes redirectAttributes,
                                           @RequestParam(value = "accessedFrom") String accessedFrom,
                                           @RequestParam(value = "idCompany", required = false) Long idCompany){

        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        if (businessService.checkBusinessName(business.getName())) {
            //Save business to DB
            businessService.addBusiness(business);
            //Success pop-up message
            redirectAttributes.addFlashAttribute("success",
                    String.format("Bidang '" + business.getName() + "' berhasil ditambahkan"));
        }
        else {
            redirectAttributes.addFlashAttribute("error",
                    String.format("Bidang '" + business.getName() + "' sudah tersedia"));
        }

        //Returned page based on accessed page
        if (accessedFrom.equals("listCompany"))
            return "redirect:/company/view/all";
        else if (accessedFrom.equals("addCompany"))
            return "redirect:/company/add";
        else {
            return "redirect:/company/update/" + idCompany;
        }
    }
    @GetMapping("/company/{idCompany}/assign_user")
    private String assignUserKlien(Model model, @PathVariable Long idCompany){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser_);
        CompanyModel company = companyService.getCompanyById(idCompany);
        model.addAttribute("company", company);

        List<CompanyUserModel> companyUserList = companyUserService.getCompanyUserByCompanyId(idCompany);
        List<UserModel> listUser = new ArrayList<UserModel>();
        for(CompanyUserModel a : companyUserList ){
            if(a.getUser().getRole().getName().equals("Klien")){
                model.addAttribute("klien",true);
                listUser.add(a.getUser());
            }
        }
        List<UserModel> listUserAll = userService.getUserList();
        List<UserModel> listKlien = new ArrayList<UserModel>();
        for(UserModel a : listUserAll) {
            if (a.getRole().getName().equals("Klien")) {
                listKlien.add(a);
            }
        }
        List<UserModel> listKlienfinal = new ArrayList<>(listKlien);
        listKlienfinal.removeAll(listUser);
        model.addAttribute("listKlien",listKlienfinal); // ini untuk tambah
        model.addAttribute("listUser", listUser); // ini list yang sudah ada
        model.addAttribute("jumlahKlien", listUser.size()+1);
        model.addAttribute("company", company);

        return "form-assign-user-klien";
    }
    @GetMapping(value = "company/{idCompany}/assign_user/remove/{id}")
    public ModelAndView deleteKonsultan(@PathVariable Long id,
                                        @PathVariable Long idCompany,
                                        Model model,
                                        RedirectAttributes redirectAttrs
                                        ) {
        UserModel user = userService.getUserById(id);
        List<ProjectUserModel> projectUserList = projectUserService.findAllByUser(user.getId());
        for (ProjectUserModel projectUser : projectUserList){
            projectUserService.removeKonsultan(projectUser.getId(), user);
        }
        companyUserService.removeKlien(id,user);
        redirectAttrs.addFlashAttribute("success",
                String.format("Konsultan dengan username "+ "`%s`" +" berhasil dihapus", user.getUsername()));
        return new ModelAndView("redirect:/company/" + idCompany +"/assign-user");
    }

    @PostMapping(value = "/company/{idCompany}/assign_user")
    private ModelAndView addKonsultanSubmit(@PathVariable Long idCompany, @RequestParam(value = "klienselect", required = true) String username, @ModelAttribute UserModel user, Model model, RedirectAttributes redirectAttrs){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User loginUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        UserModel loginUser_ = userService.getUserByUsername(loginUser.getUsername());
        CompanyUserModel companyUser = new CompanyUserModel();
        CompanyModel company = companyService.getCompanyById(idCompany);
        UserModel usermodel = userService.getUserByUsername(username);
        companyUser.setCompany(company);
        companyUser.setUser(usermodel);
        companyUser.setCreated_at(LocalDateTime.now());
        companyUser.setCreated_by(loginUser_.getId());
        List<ProjectModel> companyProjects = companyUser.getCompany().getProjectCompany();
        for (ProjectModel companyProject : companyProjects){
            ProjectUserModel projectUserNew = new ProjectUserModel();
            projectUserNew.setProject(companyProject);
            projectUserNew.setUser(usermodel);
            projectUserNew.setCreated_at(LocalDateTime.now());
            projectUserNew.setCreated_by(loginUser_.getId());
        }
        companyUserService.addCompanyUser(companyUser);
        redirectAttrs.addFlashAttribute("success", String.format("Klien dengan username "+ "`%s`" +" berhasil ditambahkan", username));
        return new ModelAndView("redirect:/company/{idCompany}/assign_user");
    }
}
