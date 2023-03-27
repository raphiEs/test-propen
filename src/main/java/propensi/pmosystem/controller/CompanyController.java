package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

        //Add CompanyUser to db
        CompanyUserModel companyUser = new CompanyUserModel();
        companyUser.setCompany(company);
        companyUser.setUser(loginUser_);
        companyUser.setCreated_at(LocalDateTime.now());
        companyUser.setCreated_by(loginUser_.getId());
        companyUserService.addCompanyUser(companyUser);

        //Add Company to db
        companyService.addCompany(company);
        //String message = "Perusahaan '" + company.getName() + "' berhasil ditambahkan";

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
        return "redirect:/company/view/all";
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
}
