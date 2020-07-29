package com.redteam.controller;

import com.redteam.model.ChangeTS;
import com.redteam.model.Form;
import com.redteam.model.LeaveOfAb;
import com.redteam.service.ChangeTSService;
import com.redteam.service.FormService;
import com.redteam.service.LeaveOfAbService;
import com.redteam.util.CurrentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserDashboardController {

	private final FormService formService;
	private final ChangeTSService changeTSService;
	private final LeaveOfAbService leaveOfAbService;

	@Autowired
	public UserDashboardController(FormService formService , ChangeTSService changeTSService, LeaveOfAbService leaveOfAbService) {
		this.formService = formService;
		this.changeTSService = changeTSService;
		this.leaveOfAbService = leaveOfAbService;
	}

	@GetMapping("/userDashboard")
	public ModelAndView userDashboard() {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("nMajorForms", this.getNMajorForms());
		modelAndView.addObject("nChangeTSForms", this.getNChangeTSForms());
		modelAndView.addObject("nLeaveOfAbForms", this.getNLeaveOfAbForms());
		modelAndView.setViewName("/userDashboard");
		return modelAndView;
	}

	private int getNMajorForms(){
		int nMajorForms = 0;
		for(Form form : formService.findAllForms()){
			if(form.getCurrentApprover().equals(CurrentState.getCurrentUsername())){
				nMajorForms++;
			}
		}
		return nMajorForms;
	}

	private int getNChangeTSForms(){
		int nChangeTSForms = 0;
		for(ChangeTS form : changeTSService.findAllForms()){
			if(form.getCurrentApprover().equals(CurrentState.getCurrentUsername())){
				nChangeTSForms++;
			}
		}
		return nChangeTSForms;
	}

	private int getNLeaveOfAbForms(){
		int nLeaveOfAbForms = 0;
		for(LeaveOfAb form : leaveOfAbService.findAllForms()){
			if(form.getCurrentApprover().equals(CurrentState.getCurrentUsername())){
				nLeaveOfAbForms++;
			}
		}
		return nLeaveOfAbForms;
	}
}
