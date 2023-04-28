package propensi.pmosystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.pmosystem.model.FeedbackModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.service.FeedbackService;
import propensi.pmosystem.service.ProjectService;
import propensi.pmosystem.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FeedbackController {
    @Autowired
    FeedbackService feedbackService;
    @Autowired
    UserService userService;
    @Autowired
    ProjectService projectService;

    @RequestMapping(value = "/{idProject}/feedback")
    private String feedbackPage(@PathVariable Long idProject, Model model){

        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        List<FeedbackModel> listFeedback = feedbackService.getListFeedbackByProject(idProject);
        model.addAttribute("listFeedback", listFeedback);
        return "project-feedback";

    }
    @GetMapping(value = "/{idProject}/feedback/add")
    private String addFeedbackFormPage(@PathVariable Long idProject, Model model){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        if (loginUser_.getRole().getId() == 4) {

            FeedbackModel feedback = new FeedbackModel();

            model.addAttribute("feedback", feedback);
            return "form-add-feedback";
        }else return "redirect:/"+idProject +"/feedback";
    }

    @PostMapping(value = "/{idProject}/feedback/add")
    private String addFeedBackSubmitPage(@PathVariable Long idProject, @ModelAttribute FeedbackModel feedback,
                                               Model model, RedirectAttributes redirectAttributes){
        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        feedback.setProject(projectService.findById(idProject));
        feedback.setCreated_by(loginUser_.getId());
        feedback.setCreated_at(LocalDateTime.now());

        feedbackService.addFeedback(feedback);

        redirectAttributes.addFlashAttribute("success", String.format("Feedback berhasil ditambahkan."));

        List<FeedbackModel> listFeedback = feedbackService.getListFeedbackByProject(idProject);
        model.addAttribute("listFeedback", listFeedback);

        return "redirect:/" + idProject + "/feedback";
    }

    @GetMapping("/{idProject}/feedback/remove/{id}")
    private String deleteFeedback(Model model,
                                  @PathVariable Long idProject,
                                  @PathVariable Long id,
                                  RedirectAttributes redirectAttributes){

        //Auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loginUser = (User) auth.getPrincipal();
        String username = loginUser.getUsername();
        UserModel loginUser_ = userService.getUserByUsername(username);

        ProjectModel project = projectService.findById(idProject);
        FeedbackModel feedback = feedbackService.getFeedbackById(id);
        project.getProjectFeedback().remove(feedback);
        feedbackService.deleteFeedback(feedback);

        redirectAttributes.addFlashAttribute("success", String.format("Feedback berhasil dihapus."));

        return "redirect:/" + idProject + "/feedback";
    }

}
