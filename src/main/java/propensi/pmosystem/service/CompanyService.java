package propensi.pmosystem.service;

import propensi.pmosystem.model.CompanyModel;

import java.util.List;

public interface CompanyService {

    CompanyModel addCompany(CompanyModel company);

    CompanyModel updateCompany(CompanyModel company);

    CompanyModel deleteCompany(CompanyModel company);

    List<CompanyModel> getListCompany();

    CompanyModel getCompanyById(Long id);
}
