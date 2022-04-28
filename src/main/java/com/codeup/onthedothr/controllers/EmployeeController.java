package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

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

    @GetMapping("/dashboard")
    public String showDashboard(Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        return "users/dashboard";
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
        if(!user.isSupervisor()){
            model.addAttribute("user", user);
            return "users/dashboard";
        }

        // View employee's profile as supervisor
        user = employeesDao.getById(id);
        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/supervisor-dashboard")
    public String showSupervisorDashboard(Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // not logged in as a supervisor
        if(!user.isSupervisor()){
            model.addAttribute("user", user);
            return "users/dashboard";
        }

        // logged in as a supervisor
        List<Employee> employees = employeesDao.findAssignedEmployees(user.getId());
        System.out.println(employees);
        model.addAttribute("user", user);
        model.addAttribute("employees", employees);
        return "users/supervisor-dashboard";
    }
}