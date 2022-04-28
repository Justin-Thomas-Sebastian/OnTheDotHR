package com.codeup.onthedothr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeliverablesController {

    @GetMapping("/details")
    public String getDeliverableDetails(Model model){
        return "/deliverables/edit";
    }
}
