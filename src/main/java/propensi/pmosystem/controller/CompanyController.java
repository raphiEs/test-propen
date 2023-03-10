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
import propensi.pmosystem.repository.BusinessDb;
import propensi.pmosystem.service.BusinessService;
import propensi.pmosystem.service.CompanyService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CompanyController {

    @Qualifier("companyServiceImpl")
    @Autowired
    private CompanyService companyService;

    @Qualifier("businessServiceImpl")
    @Autowired
    private BusinessService businessService;
    @Autowired
    private BusinessDb businessDb;

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
    private String addCompanySubmit(@ModelAttribute CompanyModel company, @RequestParam String businessId, Model model) {
        company.setCreated_at(LocalDateTime.now());
        //company.setCreated_by(null);
        if (businessId != ""){
            company.setBusiness(businessService.getBusinessById(Long.parseLong(businessId)));
        }
        companyService.addCompany(company);

        model.addAttribute("company", company);
        //log.info("Manajer berhasil menambahkan perusahaan baru");
        return "home";
    }

    @GetMapping("/company/update/{id}")
    public String updateCompanyFormPage(@PathVariable Long id,  Model model){
        CompanyModel company = companyService.getCompanyById(id);
        List<BusinessModel> listBusiness = businessService.getListBusiness();
        BusinessModel companyBusiness = company.getBusiness();

        model.addAttribute("company", company);
        model.addAttribute("listBusiness", listBusiness);
        model.addAttribute("companyBusiness", companyBusiness);
        return "form-update-company";
    }

    @PostMapping("/company/update")
    public String updateCompanySubmitPage(@ModelAttribute CompanyModel updatedCompany, @RequestParam String businessId, Model model){
        updatedCompany.setBusiness(businessService.getBusinessById(Long.parseLong(businessId)));
        companyService.updateCompany(updatedCompany);
        model.addAttribute("company", updatedCompany);
        return "/home";
    }

    @GetMapping("/company/view/all")
    public String viewAllCompany(Model model){
        List<CompanyModel> listCompany = companyService.getListCompany();

        model.addAttribute("listCompany", listCompany);

        return "view-all-company";
    }
}
