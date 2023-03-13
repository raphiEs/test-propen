package propensi.pmosystem.controller;

import java.security.Principal;
import java.util.Collection;

import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.security.UserDetailsHelper;
import propensi.pmosystem.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

@Controller
public class MainController {

    @Autowired
    private UserDetailsHelper user;
	@Autowired
    private UserService userService;
	
    @RequestMapping("/")
	public String main(Model model, HttpServletRequest req) {
		UserModel userx = userService.getUserByUsername(user.getUsername(req));
        model.addAttribute("loginUser", userx);
		return "home";
	}

	@RequestMapping("/login")
	public String login(){
		return "login";
	}

}

