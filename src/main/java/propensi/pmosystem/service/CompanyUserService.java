package propensi.pmosystem.service;

import propensi.pmosystem.model.CompanyUserModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.repository.CompanyUserDb;

public interface CompanyUserService {
    CompanyUserModel findByUser(UserModel user);
}
