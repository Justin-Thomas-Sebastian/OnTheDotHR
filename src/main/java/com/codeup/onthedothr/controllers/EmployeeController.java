package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Deliverable;
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
import java.util.List;

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

    @GetMapping("/dashboard")
    public String showDashboard(Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Deliverable> deliverables = deliverablesDao.findDeliverablesById(user.getId());
        Long supervisorId = employeesDao.getSupervisorIdById(user.getId());
        Employee supervisor = null;
        if(!(supervisorId == null)){
            supervisor = employeesDao.getById(supervisorId);
        }
        model.addAttribute("supervisor", supervisor);
        model.addAttribute("user", user);
        model.addAttribute("deliverables", deliverables);
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
        // regular employees should not be allowed to arbitrarily view other profiles through url
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

        // not logged in as a supervisor, return to employee dashboard
        if(!user.isSupervisor()){
            model.addAttribute("user", user);
            return "users/dashboard";
        }

        // logged in as a supervisor, proceed to supervisor dashboard
        List<Employee> employees = employeesDao.findAssignedEmployees(user.getId());
        List<Employee> allEmployees = employeesDao.findAll();
        model.addAttribute("user", user);
        model.addAttribute("employees", employees);
        model.addAttribute("allEmployees", allEmployees);
        return "users/supervisor-dashboard";
    }
}