package propensi.pmosystem.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import propensi.pmosystem.model.*;
import propensi.pmosystem.security.UserDetailsHelper;
import propensi.pmosystem.service.CompanyService;
import propensi.pmosystem.service.CompanyUserService;
import propensi.pmosystem.service.EventService;
import propensi.pmosystem.service.ProjectUserService;
import propensi.pmosystem.service.TimelineService;
import propensi.pmosystem.service.UserService;

@Controller
public class MainController {

    @Autowired
    private UserDetailsHelper user;
	@Autowired
    private UserService userService;
	@Autowired
    private CompanyService companyService;
	@Autowired
    private TimelineService timelineService;
	@Autowired
    private EventService eventService;
	@Autowired
    private CompanyUserService companyUserService;
	@Autowired
    private ProjectUserService projectUserService;
    @RequestMapping("/")
	public String main(Model model, HttpServletRequest req) {
		//Auth
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loginUser = (User) auth.getPrincipal();
		String username = loginUser.getUsername();
		UserModel loginUser_ = userService.getUserByUsername(username);
		UserModel userx = userService.getUserByUsername(user.getUsername(req));

		if(loginUser_.getRole().getName().equals("Manajer") || loginUser_.getRole().getName().equals("Konsultan")) {
			//total klien
			int totalklien = 0;
			List<CompanyUserModel> allCompany = companyUserService.getListCompanyUser();
			for (CompanyUserModel a : allCompany) {
				if (a.getUser().getId().equals(userx.getId())) {
					totalklien++;
				}
			}
			model.addAttribute("totalklien", totalklien);
			//total proyek
			//proyek berjalan
			//proyek selesai
			int totalproyek = 0;
			int proyekberjalan = 0;
			int proyekselesai = 0;
			List<ProjectUserModel> allProject = projectUserService.findAll();
			for (ProjectUserModel a : allProject) {
				if (a.getUser().getId().equals(userx.getId())) {
					totalproyek++;
					if (a.getProject().getStatus().equals("Sedang Berjalan")) {
						proyekberjalan++;
					}
					if (a.getProject().getStatus().equals("Sudah Selesai")) {
						proyekselesai++;
					}
				}
			}
			model.addAttribute("totalproyek", totalproyek);
			model.addAttribute("proyekberjalan", proyekberjalan);
			model.addAttribute("proyekselesai", proyekselesai);

			//projek
			List<String> list = new ArrayList<String>();
			for (ProjectUserModel a : allProject) {
				if (a.getUser().getId().equals(userx.getId())) {
					String name = a.getProject().getName();
					List<TimelineModel> timeline = timelineService.findAllByProjectId(a.getProject().getId());
					Integer weight = 0;
					for (TimelineModel b : timeline) {
						if (b.getStatus().equals("1")) {
							weight = weight + b.getWeight();

						}
					}
					String weight_String = weight.toString();
					list.add(name);
					list.add(weight_String);
				}
			}
			model.addAttribute("proyek", list);

			//event terlambat
			LocalDate today = LocalDate.now();
			List<Map<String, String>> data = new ArrayList<>();
			for (ProjectUserModel a : allProject) {
				if (a.getUser().getId().equals(userx.getId())) {
					// Long project = a.getProject().getId();
					// List<EventModel> listevent = eventService.getListEventByProjectId(project);
					List<TimelineModel> timeline = timelineService.findAllByProjectId(a.getProject().getId());
					// for (EventModel e : listevent){
					for (TimelineModel e : timeline) {
						if (e.getEndDate() != null) {
							LocalDate endDate = e.getEndDate().toLocalDate();
							if (today.isAfter(endDate) && e.getStatus().equals("0")) {
								Map<String, String> row = new HashMap<>();
								row.put("proyekid", a.getProject().getId().toString());
								row.put("proyek", a.getProject().getName());
								row.put("event", e.getMilestone_name());
								row.put("date", endDate.toString());
								data.add(row);
							}
						}
					}

				}
			}
			model.addAttribute("listterlambat", data);
			model.addAttribute("loginUser", userx);
			return "home";
		} else if (loginUser_.getRole().getName().equals("Admin")) {
			model.addAttribute("user", userx);
			model.addAttribute("loginUser", loginUser_);
			return "redirect:/user";
		}else {
			List<CompanyUserModel> listCompanyUser = companyUserService.getCompanyUserByUserId(loginUser_.getId());
			CompanyModel company = new CompanyModel();
			if (!listCompanyUser.isEmpty())
				company = listCompanyUser.get(0).getCompany();
			model.addAttribute("company", company);
			model.addAttribute("listProject", company.getProjectCompany());
			model.addAttribute("loginUser", loginUser_);
			return "redirect:/company/view/"+company.getId();
		}
	}

	@RequestMapping("/login")
	public String login(){
		return "login";
	}

}