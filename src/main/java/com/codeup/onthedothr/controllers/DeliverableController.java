package com.codeup.onthedothr.controllers;

import com.codeup.onthedothr.models.*;
import com.codeup.onthedothr.repositories.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class DeliverableController {
    private final EmployeeRepository employeesDao;
    private final DeliverableRepository deliverablesDao;
    private final CategoryRepository categoriesDao;
    private final StatusRepository statusDao;
    private final DeliverableAttachmentRepository attachmentsDao;

    public DeliverableController(
            DeliverableRepository deliverablesDao,
            EmployeeRepository employeesDao,
            CategoryRepository categoriesDao,
            StatusRepository statusDao,
            DeliverableAttachmentRepository attachmentsDao){

        this.deliverablesDao = deliverablesDao;
        this.employeesDao = employeesDao;
        this.categoriesDao = categoriesDao;
        this.statusDao = statusDao;
        this.attachmentsDao = attachmentsDao;
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
        Employee employee = employeesDao.getById(id);

        // Pass data objects to review.html
        model.addAttribute("deliverables", deliverables);
        model.addAttribute("employee", employee);
        return "/deliverables/review";
    }

    @PostMapping("/decision-deliverable/{id}")
    public String approveDenyDeliverable(@PathVariable long id, @RequestParam(name = "submit-name") String submitName, Model model){
        Deliverable deliverableToApprove = deliverablesDao.getById(id);
        Employee employee = deliverableToApprove.getEmployee();
        switch (submitName){
            case "approve":
                deliverableToApprove.setStatus(statusDao.getById(5L));
                deliverablesDao.save(deliverableToApprove);
                break;
            case "deny":
                deliverableToApprove.setStatus(statusDao.getById(2L));
                deliverablesDao.save(deliverableToApprove);
                break;
            default:
                System.out.println("Unexpected behavior");
                break;
        }
        long employeeId = employee.getId();
        return "redirect:/deliverables/" + employeeId;
    }

    @GetMapping("/details/{id}")
    public String getDeliverableDetails(@PathVariable long id, Model model){
        Deliverable deliverable = deliverablesDao.getById(id);
        List<DeliverableAttachment> attachments = attachmentsDao.findDeliverableAttachmentsByDeliverableId(deliverable.getId());
        model.addAttribute("deliverable", deliverable);
        model.addAttribute("attachments", attachments);
        return "/deliverables/edit";
    }

    @PostMapping("/deliverables/{id}/edit")
    public String editDeliverable(
            @PathVariable long id, Model model,
            @RequestParam(name = "submit-name") String submitName,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "filename1") String filename1,
            @RequestParam(name = "filename2") String filename2,
            @RequestParam(name = "filename3") String filename3,
            @RequestParam(name = "filename4") String filename4,
            @RequestParam(name = "filename5") String filename5,
            @RequestParam(name = "fileurl1") String fileurl1,
            @RequestParam(name = "fileurl2") String fileurl2,
            @RequestParam(name = "fileurl3") String fileurl3,
            @RequestParam(name = "fileurl4") String fileurl4,
            @RequestParam(name = "fileurl5") String fileurl5 ){

        Deliverable deliverableToUpdate = deliverablesDao.getById(id);
        deliverableToUpdate.setDescription(description);
        deliverableToUpdate.setLastActive(new Date()); // set current date
        deliverablesDao.save(deliverableToUpdate);

        switch (submitName){
            case "save":
                deliverableToUpdate.setStatus(statusDao.getById(2L)); // set status to 'in progress'
                deliverablesDao.save(deliverableToUpdate);
                System.out.println("saved");
                break;
            case "submit":
                deliverableToUpdate.setStatus(statusDao.getById(3L)); // set status to 'submitted'
                deliverablesDao.save(deliverableToUpdate);
                System.out.println("submitted");
                break;
            default:
                System.out.println("error encountered");
                break;
        }

        checkAndInsertAttachment(deliverableToUpdate, filename1, fileurl1);
        checkAndInsertAttachment(deliverableToUpdate, filename2, fileurl2);
        checkAndInsertAttachment(deliverableToUpdate, filename3, fileurl3);
        checkAndInsertAttachment(deliverableToUpdate, filename4, fileurl4);
        checkAndInsertAttachment(deliverableToUpdate, filename5, fileurl5);
        return "redirect:/dashboard";
    }

    @GetMapping("/deliverables/{id}/create")
    public String getCreateDeliverablesForm(@PathVariable Long id, Model model){
        List<String> categoryOptions = getCategoriesAsList();
        Employee employee = employeesDao.getById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("deliverable", new Deliverable());
        model.addAttribute("categoryOptions", categoryOptions);
        return "/deliverables/create";
    }

    @PostMapping("/deliverables/{id}/create")
    public String createNewDeliverable(
            @PathVariable Long id,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "categorySelect") String categorySelect,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "filename1") String filename1,
            @RequestParam(name = "filename2") String filename2,
            @RequestParam(name = "filename3") String filename3,
            @RequestParam(name = "filename4") String filename4,
            @RequestParam(name = "filename5") String filename5,
            @RequestParam(name = "fileurl1") String fileurl1,
            @RequestParam(name = "fileurl2") String fileurl2,
            @RequestParam(name = "fileurl3") String fileurl3,
            @RequestParam(name = "fileurl4") String fileurl4,
            @RequestParam(name = "fileurl5") String fileurl5,
            @RequestParam(name = "deadline") String deadline) throws ParseException {

        // Get category id from passed in categorySelect variable
        long categoryId;
        switch(categorySelect){
            case "onboarding":
                categoryId = 1L;
                break;
            case "notice":
                categoryId = 2L;
                break;
            case "training":
                categoryId = 3L;
                break;
            default: // generic category of "task"
                categoryId = 4L;
                break;
        }

        // Transform data from form into the format that the database requires
        Employee user = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date deadlineDate = formatter.parse(deadline);
        Category category = categoriesDao.getById(categoryId);
        Status status = statusDao.getById(1L);  // Newly created deliverable will always start as 'unopened'

        // Set data into newly created deliverable and save to database
        Deliverable deliverable = new Deliverable();
        deliverable.setTitle(title);
        deliverable.setDescription(description);
        deliverable.setSupervisor(user);
        deliverable.setEmployee(employeesDao.getById(id));
        deliverable.setCategory(category);
        deliverable.setDateCreated(new Date());  // current date
        deliverable.setDeadline(deadlineDate);
        deliverable.setStatus(status);
        deliverablesDao.save(deliverable);

        // If filename and fileurl exists, set to new DeliverableAttachment and bind to corresponding Deliverable
        checkAndInsertAttachment(deliverable, filename1, fileurl1);
        checkAndInsertAttachment(deliverable, filename2, fileurl2);
        checkAndInsertAttachment(deliverable, filename3, fileurl3);
        checkAndInsertAttachment(deliverable, filename4, fileurl4);
        checkAndInsertAttachment(deliverable, filename5, fileurl5);
        return "redirect:/supervisor-dashboard";
    }

    // If file name and file url are not null, bind to deliverable then save to database
    public void checkAndInsertAttachment(Deliverable deliverable, String filename, String fileurl){
        if(filename.length() == 0 && fileurl.length() == 0){
            System.out.println("Nothing to add.");
        } else {
            DeliverableAttachment attachment = new DeliverableAttachment(deliverable, filename, fileurl);
            attachmentsDao.save(attachment);
        }
    }

    // Utility method used to return category names. Used in an HTML select tag to display category names
    public List<String> getCategoriesAsList(){
        List<String> categoryOptions = new ArrayList<>();
        categoryOptions.add("onboarding");
        categoryOptions.add("notice ");
        categoryOptions.add("training");
        categoryOptions.add("task");
        return categoryOptions;
    }
}