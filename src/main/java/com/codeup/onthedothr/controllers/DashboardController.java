package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.*;
import com.codeup.onthedothr.repositories.AppointmentRepository;
import com.codeup.onthedothr.repositories.DeliverableRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        user = employeesDao.getById(user.getId()); // used to capture changes if profile is edited while logged in

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
        List<String> searchCategories = getSearchCategoriesAsList();
        model.addAttribute("user", user);
        model.addAttribute("employees", employees);
        model.addAttribute("allEmployees", allEmployees);
        model.addAttribute("searchCategories", searchCategories);
        return "users/supervisor-dashboard";
    }

    @GetMapping("/employees/search")
    public String searchEmployeeByName(
            @RequestParam(name = "search-input") String searchInput,
            @RequestParam(name = "search-category") String searchCategory, Model model){

        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Current logged-in user

        // If not logged in as a supervisor, return to employee dashboard
        if(!user.isSupervisor()){
            return "redirect:/dashboard";
        }

        List<Employee> employees = employeesDao.findAssignedEmployees(user.getId());
        List<String> searchCategories = getSearchCategoriesAsList();
        List<Employee> searchedEmployees = new ArrayList<>();

        switch(searchCategory){
            case "name":
                searchedEmployees = employeesDao.findEmployeesByName(searchInput);
                break;
            case "email":
                searchedEmployees = employeesDao.findEmployeesByEmail(searchInput);
                break;
            case "username":
                searchedEmployees = employeesDao.findEmployeesByUsername(searchInput);
                break;
        }

        List<Employee> allEmployees = searchedEmployees;
        model.addAttribute("user", user);
        model.addAttribute("employees", employees);
        model.addAttribute("allEmployees", allEmployees); // keeping this model name in view because it is already set there
        model.addAttribute("searchCategories", searchCategories);
        return "users/supervisor-dashboard";
    }

    // Utility method used to return employee search categories. Used in an HTML select tag to display search category names
    public List<String> getSearchCategoriesAsList(){
        List<String> categoryOptions = new ArrayList<>();
        categoryOptions.add("name");
        categoryOptions.add("email");
        categoryOptions.add("username");
        return categoryOptions;
    }
}