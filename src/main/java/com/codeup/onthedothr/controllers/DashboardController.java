package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.*;
import com.codeup.onthedothr.repositories.AppointmentRepository;
import com.codeup.onthedothr.repositories.DeliverableRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    private final EmployeeRepository employeesDao;
    private final DeliverableRepository deliverablesDao;
    private final AppointmentRepository appointmentsDao;

    public DashboardController(EmployeeRepository employeesDao, DeliverableRepository deliverablesDao, AppointmentRepository appointmentsDao) {
        this.employeesDao = employeesDao;
        this.deliverablesDao = deliverablesDao;
        this.appointmentsDao = appointmentsDao;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Current logged-in user

        // Initialize Java objects from database
        List<Deliverable> deliverables = deliverablesDao.findDeliverablesById(user.getId());
        List<Appointment> appointments = appointmentsDao.findAppointmentByUserIdAndStatusId(user.getId(), 3L); // Only 'confirmed' appointments (3L)
        List<Appointment> supervisorAppointments = new ArrayList<>();

        // If user is a supervisor, show their confirmed appointments with their assigned employees
        if(user.isSupervisor()){
            supervisorAppointments = appointmentsDao.findAppointmentBySupervisorIdAndStatusId(user.getId(), 3L); // Only 'confirmed' appointments (3L)
        }

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
        model.addAttribute("appointments", appointments);
        model.addAttribute("supervisorAppointments", supervisorAppointments);
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