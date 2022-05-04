package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Appointment;
import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.repositories.AppointmentsRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
public class AppointmentController {

    private final AppointmentsRepository appointmentsDao;
    private final EmployeeRepository employeesDao;

    public AppointmentController(AppointmentsRepository appointmentsDao, EmployeeRepository employeesDao){
        this.appointmentsDao = appointmentsDao;
        this.employeesDao = employeesDao;
    }

    @GetMapping("/request-appointment")
    public String getAppointmentRequest(){
        return "/appointments/request";
    }

    @PostMapping("/request-appointment")
    public String forwardAppointmentRequest(
            @RequestParam(name = "appointment-date") String date,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description") String description,
            Model model) throws ParseException {

        Appointment appointment = new Appointment();
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Currently logged-in user

        // Change date to format that can be inserted to db
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date appointmentDate = formatter.parse(date);

        // Update appointment object
        appointment.setEmployee(user);
        appointment.setSupervisor(user.getSupervisor());
        appointment.setTitle(title);
        appointment.setDescription(description);
        appointment.setDate(appointmentDate);

        // User Feedback. Only create appointment when employee actually has a supervisor
        String feedback = "";
        if(user.getSupervisor() == null){
            feedback = "You are currently not assigned to a supervisor. Request cancelled.";
            model.addAttribute("feedback", feedback);
            return "/appointments/request";
        }

        // The 'supervisor' variable is used to print supervisor details.
        // user.getSupervisor().getFirstName() not working because of the following error:
        // org.hibernate.LazyInitializationException
        Employee supervisor = employeesDao.getById(employeesDao.getSupervisorIdById(user.getId()));
        feedback = "Appointment request sent to " + supervisor.getFirstName() + " " + supervisor.getLastName() ;
        model.addAttribute("feedback", feedback);
        appointmentsDao.save(appointment);
        return "/appointments/request";
    }

    @GetMapping("/requests")
    public String getRequestsBucket(){
        return "appointments/requests";
    }
}
