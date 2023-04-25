package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import propensi.pmosystem.model.FeedbackModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.service.FeedbackService;
import propensi.pmosystem.service.UserService;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    FeedbackService feedbackService;
    @Autowired
    UserService userService;

    @GetMapping(value = "/add/{idProject}")
    private String addFeedbackFormPage(@PathVariable Long idProject, Model model){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        FeedbackModel feedback = new FeedbackModel();

        model.addAttribute("feedback", feedback);
        return "";
    }
}
