package propensi.pmosystem.service;

import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.repository.CompanyUserDb;

import java.util.List;

public interface CompanyUserService {
    CompanyUserModel findByUser(UserModel user);

    CompanyUserModel addCompanyUser(CompanyUserModel companyUser);

    CompanyUserModel updateCompanyUser(CompanyUserModel companyUser);

    CompanyUserModel deleteCompanyUser(CompanyUserModel companyUser);

    List<CompanyUserModel> getListCompanyUser();

    List<CompanyUserModel> getCompanyUserByUserId(Long id);

    List<CompanyUserModel> getCompanyUserByCompanyId(Long id);
}
