package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.repositories.DeliverableRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class EmployeeController {
    private final EmployeeRepository employeesDao;
    private final PasswordEncoder passwordEncoder;
    private final DeliverableRepository deliverablesDao;

    public EmployeeController(EmployeeRepository employeesDao, PasswordEncoder passwordEncoder, DeliverableRepository deliverablesDao) {
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
        user = employeesDao.getById(user.getId()); // used to capture changes if profile is edited while logged in
        model.addAttribute("user", user);
        model.addAttribute("isSupervisor", user.isSupervisor()); // used to alter return link to "Return to supervisor dashboard"
        return "users/profile";
    }

    @GetMapping("/profile/{id}")
    public String showProfileById(@PathVariable long id, Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // not logged in as a supervisor, return to employee dashboard
        // regular employees should not be allowed to arbitrarily view other profiles through url
        if(!user.isSupervisor()){
            return "redirect:/profile";
        }

        // View employee's profile from supervisor dashboard
        user = employeesDao.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("isSupervisor", true); // used to alter return link to "Return to supervisor dashboard"
        return "users/profile";
    }

    @PostMapping("/profile/{id}/edit")
    public String editProfile(
            @PathVariable long id,
            @RequestParam (name = "first-name") String firstName,
            @RequestParam (name = "last-name") String lastName,
            @RequestParam (name = "username") String username,
            @RequestParam (name = "email") String email,
            @RequestParam (name = "contact-no") String contactNo){

        Employee employee = employeesDao.getById(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setUsername(username);
        employee.setEmail(email);
        employee.setContactNo(contactNo);
        employeesDao.save(employee);
        return "redirect:/profile/" + id;
    }

    @PostMapping("/assign/{id}")
    public String assignEmployee(@PathVariable long id){
        System.out.println(id);
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee employee = employeesDao.getById(id);
        employee.setSupervisor(user);
        employeesDao.save(employee);
        System.out.println("assigned to supervisor");
        return "redirect:/supervisor-dashboard";
    }
}