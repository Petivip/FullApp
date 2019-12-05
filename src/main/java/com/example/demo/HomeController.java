package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
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

    @Autowired
    CloudinaryConfig cloudc;

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
    public String index(Model model){
        model.addAttribute("employees",employeeRepository.findAll());
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
        model.addAttribute("departments", departmentRepository.findAll());

        return "employee";
    }

    @PostMapping("/processemployee")
    public String processEmployee(@ModelAttribute Employee employee, @RequestParam("departmentId") long id,@RequestParam("file") MultipartFile file){
        Department department = departmentRepository.findById(id).get();

        // save department in employee and set the employee variables
        employee.setDepartment(department);
        if (file.isEmpty()){
            employeeRepository.save(employee);
            return "redirect:/";
        }
        try{
            Map uploadResult=cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype","auto"));
            employee.setPic(uploadResult.get("url").toString());
            employeeRepository.save(employee);
        } catch (IOException e){
            e.printStackTrace();
            return "redirect:/addemployee";
        }


        return "redirect:/";
    }

    @GetMapping("/adddepartment")
    public String addDepartment(Model model){
        model.addAttribute("department", new Department());
        model.addAttribute("employees", employeeRepository.findAll());
        return "department";
    }

    @PostMapping("/processdepartment")
    public String processDepartment(@ModelAttribute Department department){

        departmentRepository.save(department);
        return "redirect:/";
    }

    //Delete, Update and detail
    @RequestMapping("/detail/{id}")
    public String ViewDetail(@PathVariable("id") long id, Model model){
        model.addAttribute("employee",employeeRepository.findById(id).get());
        return "detail";
    }
    @RequestMapping("/update/{id}")
    public String Update(@PathVariable("id") long id, Model model){
        model.addAttribute("employee",employeeRepository.findById(id).get());
        model.addAttribute("departments", departmentRepository.findAll());
        return "employee";
    }
    @RequestMapping("/delete/{id}")
    public String Delete(@PathVariable("id") long id){
        employeeRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/searchlist")
    public String SearchPage(Model model, @RequestParam("search") String search) {
        model.addAttribute("employees",employeeRepository.findByFirstNameContainingIgnoreCase(search));
        return "searchindex";
    }
}