package com.codeup.onthedothr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AppointmentController {
    @GetMapping("/request-appointment")
    public String getAppointmentRequest(){
        return "/users/request";
    }

    @PostMapping("/request-appointment")
    public String forwardAppointmentRequest(Model model){
        String feedback = "Request sent.";
        model.addAttribute("feedback", feedback);
        return "/users/request";
    }

    @GetMapping("/requests")
    public String getRequestsBucket(){
        return "appointments/requests";
    }
}
