package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Deliverable;
import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.models.Status;
import com.codeup.onthedothr.repositories.DeliverablesRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DeliverablesController {
    private final EmployeeRepository employeesDao;
    private final DeliverablesRepository deliverablesDao;

    public DeliverablesController(DeliverablesRepository deliverablesDao, EmployeeRepository employeesDao){
        this.deliverablesDao = deliverablesDao;
        this.employeesDao = employeesDao;
    }

    @GetMapping("/details")
    public String getDeliverableDetails(Model model){
        return "/deliverables/edit";
    }

    @GetMapping("/deliverables/{id}")
    public String getDeliverablesById(@PathVariable Long id, Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // not logged in as a supervisor, return to employee dashboard
        if(!user.isSupervisor()){
            model.addAttribute("user", user);
            return "users/dashboard";
        }

        // logged in as a supervisor, proceed to employee's current deliverables
        List<Deliverable> deliverables = deliverablesDao.findDeliverablesById(id);
        Status status = new Status(); // Send empty Status object to view, so getStatus() can be called with current deliverable's status_id
        Employee employee = employeesDao.getById(id);
        model.addAttribute("deliverables", deliverables);
        model.addAttribute("employee", employee);
        model.addAttribute("status", status);
        return "/deliverables/show";
    }

    @GetMapping("/deliverables/{id}/create")
    public String getCreateDeliverablesForm(@PathVariable Long id, Model model){
        Employee employee = employeesDao.getById(id);
        model.addAttribute("employee", employee);
        return "deliverables/create";
    }
}