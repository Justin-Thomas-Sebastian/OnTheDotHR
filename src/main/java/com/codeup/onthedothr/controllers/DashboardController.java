package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Category;
import com.codeup.onthedothr.models.Deliverable;
import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.models.Status;
import com.codeup.onthedothr.repositories.DeliverablesRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class DashboardController {
    private final EmployeeRepository employeesDao;
    private final DeliverablesRepository deliverablesDao;

    public DashboardController(EmployeeRepository employeesDao, DeliverablesRepository deliverablesDao) {
        this.employeesDao = employeesDao;
        this.deliverablesDao = deliverablesDao;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Currently logged-in user

        // Initialize Java objects from database
        List<Deliverable> deliverables = deliverablesDao.findDeliverablesById(user.getId());
        Long supervisorId = employeesDao.getSupervisorIdById(user.getId());
        Status status = new Status(); // Send empty Status object to view, so getStatus() can be called with status_id
        Category category = new Category(); // Send empty Category object to view, so getCategory() can be called with category_id

        // If employee is assigned a supervisor, retrieve that supervisor
        Employee supervisor = null;
        if(!(supervisorId == null)){
            supervisor = employeesDao.getById(supervisorId);
        }

        // Pass data objects to employee dashboard view
        model.addAttribute("supervisor", supervisor);
        model.addAttribute("user", user);
        model.addAttribute("deliverables", deliverables);
        model.addAttribute("status", status);
        model.addAttribute("category", category);
        return "users/dashboard";
    }

    @GetMapping("/supervisor-dashboard")
    public String showSupervisorDashboard(Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Current logged-in user

        // If not logged in as a supervisor, return to employee dashboard
        if(!user.isSupervisor()){
            return "redirect:/dashboard";
        }

        // Initialize Java objects from database
        List<Employee> employees = employeesDao.findAssignedEmployees(user.getId());
        List<Employee> allEmployees = employeesDao.findAll();

        // Pass data objects to employee dashboard view
        model.addAttribute("user", user);
        model.addAttribute("employees", employees);
        model.addAttribute("allEmployees", allEmployees);
        return "users/supervisor-dashboard";
    }
}