package propensi.pmosystem.controller;

import java.security.Principal;
import java.util.Collection;
import propensi.pmosystem.security.UserDetailsHelper;

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

	
    @RequestMapping("/")
	public String main(Model model, HttpServletRequest req) {
		// String name = request.getRemoteUser();
		// Principal role = request.getUserPrincipal();
		user.req = req;
		String name = user.getUsername(req);
		GrantedAuthority role = user.getRole(req);
		model.addAttribute("name", name);
		model.addAttribute("role", role);
		return "home";
	}

	@RequestMapping("/login")
	public String login(){
		return "login";
	}

}

