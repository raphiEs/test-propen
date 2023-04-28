package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.ProjectUserModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.repository.CompanyUserDb;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CompanyUserServiceImpl implements CompanyUserService{
    @Autowired
    CompanyUserDb companyUserDb;

    @Override
    public CompanyUserModel findByUser(UserModel user){
        return companyUserDb.findByUser(user);
    }

    @Override
    public CompanyUserModel addCompanyUser(CompanyUserModel companyUser) {
        companyUserDb.save(companyUser);
        return companyUser;
    }

    @Override
    public CompanyUserModel updateCompanyUser(CompanyUserModel companyUser) {
        companyUserDb.save(companyUser);
        return companyUser;
    }

    @Override
    public CompanyUserModel deleteCompanyUser(CompanyUserModel companyUser) {
        companyUserDb.delete(companyUser);
        return companyUser;
    }

    @Override
    public List<CompanyUserModel> getListCompanyUser() {
        List<CompanyUserModel> companyUserList = companyUserDb.findAll();
        return companyUserList;
    }

    @Override
    public List<CompanyUserModel> getCompanyUserByUserId(Long id) {
        return companyUserDb.findByUserId(id);
    }

    @Override
    public List<CompanyUserModel> getCompanyUserByCompanyId(Long id) {
        return companyUserDb.findByCompanyId(id);
    }

    @Override
    public void removeKlien(Long id, UserModel user) {
        List<CompanyUserModel> list = companyUserDb.findByCompanyId(id);
        for(CompanyUserModel x: list){
            if (x.getUser().equals(user)){
                companyUserDb.delete(x);
            }
        }
    }


}
