package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid
                                          @ModelAttribute("user") User user, BindingResult result,
                                          Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "index";
    }


    @RequestMapping("/")
    public String index(){
        return "index";
    }


    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }



    // adding employee and company



    @GetMapping("/addemployee")
    public String addEmployee(Model model){
        model.addAttribute("employee", new Employee());
        return "employee";
    }

    @PostMapping("/processemployee")
    public String processEmployee(@ModelAttribute Employee employee){
        employeeRepository.save(employee);
        return "redirect:/";
    }

    @GetMapping("/adddepartment")
    public String addDepartment(Model model){
        model.addAttribute("department", new Department());
        model.addAttribute("employees", employeeRepository.findAll());
        return "department";
    }

    @PostMapping("/processdepartment")
    public String processDepartment(@ModelAttribute Department department, @RequestParam("departmentId") long id){
        Employee employee = employeeRepository.findById(id).get();
        // Iterable<Book> books = bookRepository.findAll();
        Set<Employee> employ;
        if(department.employees != null){
             employ= new HashSet<>(department.employees);
        }
        else{
            employ = new HashSet<>();
        }
        employ.add(employee);
        department.setEmployees(employ);
        departmentRepository.save(department);
        return "redirect:/";
    }

}