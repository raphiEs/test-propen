package propensi.pmosystem.service;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.repository.UserDb;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDb userDb;

    @Override
    public UserModel addUser(UserModel user) {
        String pass = encrypt(user.getPassword());
        user.setPassword(pass);
        return userDb.save(user);
    }

    @Override
    public String encrypt(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        return  hashedPassword;
    }

    @Override
    public List<UserModel> getUserList(){ return userDb.findAll();}

    @Override
    public UserModel getUserByUsername(String username) {
        UserModel user = userDb.findByUsername(username);
        return user;
    }

    @Override
    public void removeUser(UserModel user) {
        userDb.delete(user);
    }
}