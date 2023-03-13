package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import propensi.pmosystem.model.BusinessModel;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.repository.BusinessDb;
import propensi.pmosystem.service.BusinessService;
import propensi.pmosystem.service.CompanyService;
import propensi.pmosystem.service.ProjectService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CompanyController {

    @Qualifier("companyServiceImpl")
    @Autowired
    private CompanyService companyService;

    @Qualifier("businessServiceImpl")
    @Autowired
    private BusinessService businessService;

    @Qualifier("projectServiceImpl")
    @Autowired
    private ProjectService projectService;

    @GetMapping("/company/add")
    private String addCompanyFormPage(Model model) {
        CompanyModel company = new CompanyModel();
        List<BusinessModel> listBusiness = businessService.getListBusiness();

        model.addAttribute("company", company);
        model.addAttribute("listBusiness", listBusiness);
        //log.info("Manajer memulai proses 'tambah perusahaan'");
        return "form-add-company";
    }

    @PostMapping("/company/add")
    private String addCompanySubmit(@ModelAttribute CompanyModel company,
                                    @RequestParam String businessId,
                                    Model model) {

        company.setCreated_at(LocalDateTime.now());
        //company.setCreated_by(null);
        if (businessId != ""){
            company.setBusiness(businessService.getBusinessById(Long.parseLong(businessId)));
        }
        companyService.addCompany(company);
        String message = "Perusahaan '" + company.getName() + "' berhasil ditambahkan";

        model.addAttribute("message", message);
        return "form-success";
    }

    @GetMapping("/company/update/{id}")
    public String updateCompanyFormPage(@PathVariable Long id,
                                        Model model){
        CompanyModel company = companyService.getCompanyById(id);
        List<BusinessModel> listBusiness = businessService.getListBusiness();
        BusinessModel companyBusiness = company.getBusiness();

        model.addAttribute("company", company);
        model.addAttribute("listBusiness", listBusiness);
        model.addAttribute("companyBusiness", companyBusiness);
        return "form-update-company";
    }

    @PostMapping("/company/update")
    public String updateCompanySubmitPage(@ModelAttribute CompanyModel updatedCompany,
                                          @RequestParam String businessId,
                                          Model model){
        updatedCompany.setBusiness(businessService.getBusinessById(Long.parseLong(businessId)));
        companyService.updateCompany(updatedCompany);
        String message = "Klien dengan nama '" + updatedCompany.getName() + "' berhasil diperbarui";

        model.addAttribute("message", message);
        return "form-success";
    }

    @GetMapping("/company/view/all")
    public String viewAllCompany(Model model){
        List<CompanyModel> listCompany = companyService.getListCompany();

        model.addAttribute("listCompany", listCompany);
        return "view-all-company";
    }

    @GetMapping("/company/view/{id}")
    public String viewDetailCompany(@PathVariable Long id,
                                    Model model){
        CompanyModel company = companyService.getCompanyById(id);
        List<ProjectModel> listProject = company.getProjectCompany();
        //String message = listProject.get(0).getName();

        //model.addAttribute("message", message);
        model.addAttribute("company", company);
        model.addAttribute("listProject", listProject);
        return "view-detail-company";
    }

    @GetMapping("/company/project/add/{id}")
    public String addCompanyProjectForm(@PathVariable Long id,
                                        Model model){
        CompanyModel company = companyService.getCompanyById(id);
        List<ProjectModel> listProject = projectService.findAll();

        model.addAttribute("company", company);
        model.addAttribute("listProject", listProject);
        return "form-add-company-project";
    }

    @PostMapping("/company/project/add")
    public String addCompanyProjectFormSubmitPage(@ModelAttribute CompanyModel company,
                                                  @RequestParam(value = "kodeProyeks") String[] kodeProyeks,
                                                  @RequestParam String businessId,
                                                  Model model){
            //Mengisi list proyek milik klien
            //String message = "";
            List<ProjectModel> tempListProjectCompany = new ArrayList<>();
            for (int i = 0 ; i<kodeProyeks.length ; i++){
                ProjectModel tempProject = projectService.findById(Long.parseLong(kodeProyeks[i]));
                tempProject.setCompany(company);
                tempListProjectCompany.add(tempProject);

            }
            company.setProjectCompany(tempListProjectCompany);
            company.setBusiness(businessService.getBusinessById(Long.parseLong(businessId)));
            companyService.updateCompany(company);

            String message = "Proyek berhasil ditambahkan pada klien '"+ company.getName() +"'";
            model.addAttribute("message", message);
            return "form-success";
    }
}
