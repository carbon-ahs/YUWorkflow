package com.redteam.controller;

import com.redteam.model.ChangeTS;
import com.redteam.model.CommentHolder;
import com.redteam.model.Form;
import com.redteam.model.LeaveOfAb;
import com.redteam.service.ChangeTSService;
import com.redteam.service.EmailService;
import com.redteam.service.FormService;
import com.redteam.service.LeaveOfAbService;
import com.redteam.service.ProductService;
import com.redteam.service.ShoppingCartService;
import com.redteam.util.CurrentState;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final FormService formService;
    private final ChangeTSService changeTSService;
    private final LeaveOfAbService leaveOfAbService;
    private final EmailService emailService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, ProductService productService, 
    		FormService formService, ChangeTSService changeTSService, LeaveOfAbService leaveOfAbService, 
    		EmailService emailService) {
        this.shoppingCartService = shoppingCartService;
        this.formService = formService;
        this.changeTSService = changeTSService;
        this.leaveOfAbService = leaveOfAbService;
        this.emailService = emailService;
    }

    @GetMapping("/shoppingCart")
    public ModelAndView shoppingCart() {
        ModelAndView modelAndView = new ModelAndView();
        Form form = shoppingCartService.getForm();
        ChangeTS changeTS = shoppingCartService.getChangeTSForm();
        LeaveOfAb leaveOfAb = shoppingCartService.getLeaveOfAbForm();
        if (form != null) {
            modelAndView.addObject("form", form);
        	modelAndView.addObject("comments", form.getCommentsArray());
        }
        if (changeTS != null) {
            modelAndView.addObject("changeTS", changeTS);
            modelAndView.addObject("comments", changeTS.getCommentsArray());
        }
        if (leaveOfAb != null) {
            modelAndView.addObject("leaveOfAb", leaveOfAb);
            modelAndView.addObject("comments", leaveOfAb.getCommentsArray());   
        }     
        CommentHolder commentHolder = new CommentHolder();
		modelAndView.addObject("commentHolder", commentHolder);
		modelAndView.setViewName("/shoppingCart");
        return modelAndView;
    }
    
    @RequestMapping(value = "/shoppingCart", method = RequestMethod.POST)
	public String formSubmit(@Valid CommentHolder commentHolder, BindingResult bindingResult) {
		Form form = shoppingCartService.getForm();
        ChangeTS changeTS = shoppingCartService.getChangeTSForm();
        LeaveOfAb leaveOfAb = shoppingCartService.getLeaveOfAbForm();
		if (form != null) {
			form.addComment(CurrentState.getCurrentUsername(), commentHolder.getComment());
			formService.saveForm(form);
		}
		if (changeTS != null) {
			changeTS.addComment(CurrentState.getCurrentUsername(), commentHolder.getComment());
			changeTSService.saveForm(changeTS);
		}
		if (leaveOfAb != null) {
			leaveOfAb.addComment(CurrentState.getCurrentUsername(), commentHolder.getComment());
			leaveOfAbService.saveForm(leaveOfAb);
		}
		return "redirect:/shoppingCart/";
	}
    
    @GetMapping("/shoppingCart/processForm/{formId}")
    public String addFormToCart(@PathVariable("formId") Long formId) {
        formService.findById(formId).ifPresent(shoppingCartService::addForm);
        return "redirect:/shoppingCart";
    }
    
    @GetMapping("/shoppingCart/processChangeTSForm/{formId}")
    public String addChangeTSFormToCart(@PathVariable("formId") Long formId) {
        changeTSService.findById(formId).ifPresent(shoppingCartService::addChangeTSForm);
        return "redirect:/shoppingCart";
    }
    
    @GetMapping("/shoppingCart/processLeaveOfAbForm/{formId}")
    public String addLeaveOfAbFormToCart(@PathVariable("formId") Long formId) {
        leaveOfAbService.findById(formId).ifPresent(shoppingCartService::addLeaveOfAbForm);
        return "redirect:/shoppingCart";
    }
    
    @GetMapping("/shoppingCart/approveForm/{formId}")
    public String approveForm(@PathVariable("formId") Long formId) throws MessagingException {
        formService.findById(formId).ifPresent(shoppingCartService::approveForm);
        emailService.sendNextMessage(formId);
        return "redirect:/home";
    }
    
    @GetMapping("/shoppingCart/approveChangeTSForm/{formId}")
    public String approveChangeTSForm(@PathVariable("formId") Long formId) throws MessagingException {
        changeTSService.findById(formId).ifPresent(shoppingCartService::approveChangeTSForm);
        emailService.sendNextChangeTSMessage(formId);
        return "redirect:/homeForTSForms";
    }
    
    @GetMapping("/shoppingCart/approveLeaveOfAbForm/{formId}")
    public String approveLeaveOfAbForm(@PathVariable("formId") Long formId) throws MessagingException {
        leaveOfAbService.findById(formId).ifPresent(shoppingCartService::approveLeaveOfAbForm);
        emailService.sendNextLeaveOfAbMessage(formId);
        return "redirect:/homeForLeaveOfAb";
    }
    
    @GetMapping("/shoppingCart/denyForm/{formId}")
    public String denyForm(@PathVariable("formId") Long formId) {
        Form form = formService.findById(formId).orElse(null);
    	if (form != null) shoppingCartService.denyForm(form);
        try {
			emailService.sendStudentDenialMessage(form.getStudentEmail(), form.getTrackingId(), form.getDenyer());;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
        return "redirect:/home";
    }
    
    @GetMapping("/shoppingCart/denyChangeTSForm/{formId}")
    public String denyChangeTSForm(@PathVariable("formId") Long formId) {
    	ChangeTS form = changeTSService.findById(formId).orElse(null);
    	if (form != null) shoppingCartService.denyChangeTSForm(form);
        try {
			emailService.sendStudentDenialMessage(form.getStudentEmail(), form.getTrackingId(), form.getDenyer());;
		} catch (MessagingException e) {
			e.printStackTrace();
		}		
        return "redirect:/homeForTSForms";
    }
    
    @GetMapping("/shoppingCart/denyLeaveOfAbForm/{formId}")
    public String denyLeaveOfAbForm(@PathVariable("formId") Long formId) {
    	LeaveOfAb form = leaveOfAbService.findById(formId).orElse(null);
    	if (form != null) shoppingCartService.denyLeaveOfAbForm(form);
        try {
			emailService.sendStudentDenialMessage(form.getStudentEmail(), form.getTrackingId(), form.getDenyer());;
		} catch (MessagingException e) {
			e.printStackTrace();
		}		
        return "redirect:/homeForLeaveOfAb";
    }

}
