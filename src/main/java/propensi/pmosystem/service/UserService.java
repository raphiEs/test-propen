package propensi.pmosystem.service;

import java.util.List;

import propensi.pmosystem.model.UserModel;

public interface UserService {
    UserModel addUser(UserModel user);
    public String encrypt(String password);
    public List<UserModel> getUserList();
    UserModel getUserByUsername(String username);

    UserModel getUserById(Long id);
    public List<UserModel> getUserByRole(Long role);
    public void removeUser(UserModel user);
    Boolean getMatchPassword(String passwordInput, String passwordDatabase);
    void updateUser(UserModel myUser);
}
