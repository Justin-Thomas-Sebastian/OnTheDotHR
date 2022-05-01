package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.repositories.DeliverablesRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class EmployeeController {
    private final EmployeeRepository employeesDao;
    private final PasswordEncoder passwordEncoder;
    private final DeliverablesRepository deliverablesDao;

    public EmployeeController(EmployeeRepository employeesDao, PasswordEncoder passwordEncoder, DeliverablesRepository deliverablesDao) {
        this.employeesDao = employeesDao;
        this.passwordEncoder = passwordEncoder;
        this.deliverablesDao = deliverablesDao;
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

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/profile/{id}")
    public String showProfileById(@PathVariable long id, Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // not logged in as a supervisor, return to employee dashboard
        // regular employees should not be allowed to arbitrarily view other profiles through url
        if(!user.isSupervisor()){
            return "redirect:/dashboard";
        }

        // View employee's profile from supervisor dashboard
        user = employeesDao.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("isSupervisor", true); // used to alter return link to "Return to supervisor dashboard"
        return "users/profile";
    }
}