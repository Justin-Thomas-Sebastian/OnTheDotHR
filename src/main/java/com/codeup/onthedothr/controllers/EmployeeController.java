package com.codeup.onthedothr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {
    @GetMapping("/sign-up")
    public String showSignupForm(Model model){
//        model.addAttribute("employee", new Employee());
        return "users/sign-up";
    }
}
