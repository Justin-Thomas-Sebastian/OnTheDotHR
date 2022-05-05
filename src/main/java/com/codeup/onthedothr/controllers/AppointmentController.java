package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.Appointment;
import com.codeup.onthedothr.models.AppointmentStatus;
import com.codeup.onthedothr.models.Employee;
import com.codeup.onthedothr.repositories.AppointmentStatusRepository;
import com.codeup.onthedothr.repositories.AppointmentRepository;
import com.codeup.onthedothr.repositories.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.sql.Time;

@Controller
public class AppointmentController {

    private final AppointmentRepository appointmentsDao;
    private final EmployeeRepository employeesDao;
    private final AppointmentStatusRepository appointmentStatusDao;

    public AppointmentController(
            AppointmentRepository appointmentsDao,
            EmployeeRepository employeesDao,
            AppointmentStatusRepository appointmentStatusDao){

        this.appointmentsDao = appointmentsDao;
        this.employeesDao = employeesDao;
        this.appointmentStatusDao = appointmentStatusDao;
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
            @RequestParam(name = "appointment-time") String appointmentTimeStr,
            @RequestParam(name = "location") String location,
            Model model) throws ParseException {

        Appointment appointment = new Appointment();
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Current logged-in user
        AppointmentStatus defaultStatus = appointmentStatusDao.getById(1L); // Default status is 'pending"

        // Change date and time to format that can be inserted to db
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        Date appointmentDate = dateFormatter.parse(date);
        Time appointmentTime = new Time(timeFormatter.parse(appointmentTimeStr).getTime());

        // Update appointment object
        appointment.setEmployee(user);
        appointment.setSupervisor(user.getSupervisor());
        appointment.setTitle(title);
        appointment.setDescription(description);
        appointment.setDate(appointmentDate);
        appointment.setStatus(defaultStatus);
        appointment.setTime(appointmentTime);
        appointment.setLocation(location);

        // User Feedback. Only insert appointment to db when employee actually has a supervisor
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
    public String getEmployeeAppointments(Model model){
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // not logged in as a supervisor, return to employee dashboard
        if(!user.isSupervisor()){
            model.addAttribute("user", user);
            return "users/dashboard";
        }

        // logged in as a supervisor, proceed to my employee's appointment requests
        // should only show 'pending' appointments (status_id of 1L)
        List<String> statusOptions = getStatusAsList();
        List<Appointment> appointmentRequestList = appointmentsDao.findAppointmentBySupervisorIdAndStatusId(user.getId(), 1L);
        model.addAttribute("appointmentRequestList", appointmentRequestList);
        model.addAttribute("statusOptions", statusOptions);
        return "appointments/requests";
    }

    @PostMapping("/appointments/{id}/update-status")
    public String updateAppointmentStatus(@PathVariable long id, @RequestParam(name = "statusSelect") String status){
        long newStatusId;
        switch(status){
            case "denied":
                newStatusId = 2L;
                break;
            case "confirmed":
                newStatusId = 3L;
                break;
            default:
                newStatusId = 1L; // keep status as 'pending'
                break;
        }

        // Update appointment status and save to db
        AppointmentStatus newStatus = appointmentStatusDao.getById(newStatusId);
        Appointment appointmentToUpdate = appointmentsDao.getById(id);
        appointmentToUpdate.setStatus(newStatus);
        appointmentsDao.save(appointmentToUpdate);
        return "redirect:/requests";
    }

    @PostMapping("/cancel-appointment/{id}")
    public String cancelAppointment(@PathVariable long id){
        Appointment appointmentToCancel = appointmentsDao.getById(id);
        AppointmentStatus newStatus = appointmentStatusDao.getById(4L); // 4L is 'cancelled' status
        appointmentToCancel.setStatus(newStatus);
        appointmentsDao.save(appointmentToCancel);
        return "redirect:/dashboard";
    }

    @GetMapping("/appointments/{id}/manage")
    public String getEmployeeAppointmentDetails(@PathVariable long id, Model model){
        Employee employee = employeesDao.getById(id);
        List<Appointment> appointments = appointmentsDao.findAppointmentByUserIdAndStatusId(id, 3L); // only confirmed appointments
        model.addAttribute("employee", employee);
        model.addAttribute("appointments", appointments);
        return "/appointments/manage";
    }

    @GetMapping("/appointments/{id}/create")
    public String getCreateAppointmentForm(@PathVariable long id, Model model){
        Employee supervisor = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee employee = employeesDao.getById(id);
        model.addAttribute("supervisor", supervisor);
        model.addAttribute("employee", employee);
        return "/appointments/create";
    }

    @PostMapping("/appointments/{id}/create")
    public String createNewAppointment(
            @PathVariable long id,
            @RequestParam(name = "appointment-date") String date,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "appointment-time") String appointmentTimeStr,
            @RequestParam(name = "location") String location,
            Model model) throws ParseException{

        Appointment appointment = new Appointment();
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Current logged-in user
        AppointmentStatus confirmedStatus = appointmentStatusDao.getById(3L); // Set to 'confirmed'

        // Change date and time to format that can be inserted to db
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        Date appointmentDate = dateFormatter.parse(date);
        Time appointmentTime = new Time(timeFormatter.parse(appointmentTimeStr).getTime());

        // Update appointment object
        appointment.setEmployee(employeesDao.getById(id));
        appointment.setSupervisor(user);
        appointment.setTitle(title);
        appointment.setDescription(description);
        appointment.setDate(appointmentDate);
        appointment.setStatus(confirmedStatus);
        appointment.setTime(appointmentTime);
        appointment.setLocation(location);
        appointmentsDao.save(appointment);
        return "redirect:/supervisor-dashboard";
    }

    // Utility method used to return status names. Used in an HTML select tag to display status names
    public List<String> getStatusAsList(){
        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("pending");
        statusOptions.add("denied");
        statusOptions.add("confirmed");
        return statusOptions;
    }
}