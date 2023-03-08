package propensi.pmosystem.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    
    @RequestMapping("/")
	public String main() {
		return "home";
	}

	@RequestMapping("/login")
	public String login(){
		return "login";
	}

}

