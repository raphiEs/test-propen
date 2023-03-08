package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import java.util.List;

import propensi.pmosystem.service.UserService;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.security.UserDetailsServiceImpl;
import propensi.pmosystem.model.RoleModel;
import propensi.pmosystem.service.RoleService;


@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    // @GetMapping("/")
    // public String viewUserAccountForm(
    //         @AuthenticationPrincipal UserDetailsServiceImpl userDetails,
    //         Model model) {
    //     String username = userDetails.getUsername();
    //     UserModel user = userService.getUserByUsername(username);
         
    //     model.addAttribute("user", user);         
    //     return "account";
    // }
   

    @GetMapping(value = "/add")
    private String addUserFormPage(Model model){
        UserModel user = new UserModel();
        List<RoleModel> listRole = roleService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("listRole",listRole);
        return "form-add-user";
    }

    @PostMapping(value = "/add")
    private String addUserSubmit(@ModelAttribute UserModel user, Model model){
        userService.addUser(user);
        model.addAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping(value = "/viewall")
    public String listUser(Model model){
        List<UserModel> listUser = userService.getUserList();
        model.addAttribute("listUser", listUser);
        return "viewall-user";
    }

    @GetMapping(value = "/hapus/{username}")
    public String deleteUserForm(@PathVariable String username, Model model) {
        UserModel user = userService.getUserByUsername(username);
        List<UserModel> listUser = userService.getUserList();
        userService.removeUser(user);
        model.addAttribute("listUser", listUser);
        return "redirect:/user/viewall";
    }
    
}
