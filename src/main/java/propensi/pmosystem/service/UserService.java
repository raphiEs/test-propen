package propensi.pmosystem.service;

import java.util.List;

import propensi.pmosystem.model.UserModel;

public interface UserService {
    UserModel addUser(UserModel user);
    public String encrypt(String password);
    public List<UserModel> getUserList();
    UserModel getUserByUsername(String username);
    public List<UserModel> getUserByRole(Long role);
    public void removeUser(UserModel user);
}
