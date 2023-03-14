package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import java.util.List;

import propensi.pmosystem.service.UserService;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.security.UserDetailsHelper;
import propensi.pmosystem.model.RoleModel;
import propensi.pmosystem.service.RoleService;


@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserDetailsHelper user;

    @GetMapping("/")
    public String viewUserAccountForm(Model model, HttpServletRequest req) {
        UserModel userx = userService.getUserByUsername(user.getUsername(req));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(username);
        model.addAttribute("user", userx);
        model.addAttribute("loginUser", loginUser);
        return "account";
    }

    @GetMapping(value = "/update-account")
    public String updateAccountForm(Model model, HttpServletRequest req){
        UserModel userx = userService.getUserByUsername(user.getUsername(req));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(username);
        model.addAttribute("user", userx);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("message", "");
        return "form-update-account";
    }
    
    @PostMapping(value = "/update-account")
    public String updateAccount(Model model, @ModelAttribute UserModel useruser, HttpServletRequest req){
        UserModel userx = userService.getUserByUsername(user.getUsername(req));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(username);
        userx.setEmail(useruser.getEmail());
        userx.setContact(useruser.getContact());
        userService.updateUser(userx);
        model.addAttribute("user", userx);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("message", "Data berhasil diubah!");
        return "account";
    }

    @GetMapping(value = "/update-password")
    public String updatePasswordForm(Model model, HttpServletRequest req){
        UserModel userx = userService.getUserByUsername(user.getUsername(req));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(username);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("user", userx);  
        model.addAttribute("message", "");
        return "reset-password";
    }

    @PostMapping(value = "/update-password")
    public String updatePassword(@ModelAttribute UserModel userModel, String username, String password, String newPassword, String konfirmasiPassword, Model model){
        UserModel userx = userService.getUserByUsername(username);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String uname = user.getUsername();
        UserModel loginUser = userService.getUserByUsername(uname);
        model.addAttribute("user", userx);
        model.addAttribute("loginUser", loginUser);
        if (userService.getMatchPassword(password, userx.getPassword())){
            // System.out.println("hai");
            if (newPassword.equals(konfirmasiPassword)){
                System.out.println("Password Konfirmasi Sama");
                String newPasswordHash = userService.encrypt(newPassword);
                userx.setPassword(newPasswordHash);
                userService.updateUser(userx);
                model.addAttribute("message", "password berhasil diubah");
            }else {
                // System.out.println("hai 2");
                model.addAttribute("message", "password tidak sama");
            }
        }else {
            // System.out.println("hai 3")
            model.addAttribute("message", "password salah");
        }
        return "reset-password";
    }

    @GetMapping(value = "/add")
    private String addUserFormPage(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);
        UserModel user = new UserModel();
        List<RoleModel> listRole = roleService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("listRole",listRole);
        model.addAttribute("loginUser",loginUser_);
        return "form-add-user";
    }

    @PostMapping(value = "/add")
    private String addUserSubmit(@ModelAttribute UserModel user, Model model){
        userService.addUser(user);
        model.addAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping(value = "/viewall")
    public String listUser(Model model, HttpServletRequest req){
        List<UserModel> listUser = userService.getUserList();
        model.addAttribute("listUser", listUser);
        UserModel userx = userService.getUserByUsername(user.getUsername(req));
        model.addAttribute("loginUser", userx);
        return "viewall-user";
    }

    @GetMapping(value = "/remove/{username}")
    public String deleteUserForm(@PathVariable String username, Model model) {
        UserModel user = userService.getUserByUsername(username);
        List<UserModel> listUser = userService.getUserList();
        userService.removeUser(user);
        model.addAttribute("listUser", listUser);
        return "redirect:/user/viewall";
    }
    
}