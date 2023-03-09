package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.repository.CompanyUserDb;

@Transactional
@Service
public class CompanyUserServiceImpl implements CompanyUserService{
    @Autowired
    CompanyUserDb companyUserDb;

    @Override
    public CompanyUserModel findByUser(UserModel user){
        return companyUserDb.findByUser(user);
    }

}
