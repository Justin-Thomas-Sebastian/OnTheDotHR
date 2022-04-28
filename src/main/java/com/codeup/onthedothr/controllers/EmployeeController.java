package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class EmployeeController {

    private final EmployeeRepository employeesDao;
    private final PasswordEncoder passwordEncoder;

    public EmployeeController(EmployeeRepository employeesDao, PasswordEncoder passwordEncoder) {
        this.employeesDao = employeesDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/sign-up")
    public String showSignupForm(Model model){
        model.addAttribute("employee", new Employee());
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String saveEmployee(@ModelAttribute Employee employee){
        String hash = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(hash);
        employeesDao.save(employee);
        return "redirect:/login";
    }
}
