package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public String listEmployees(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute(
                "employees",
                employeeService.searchEmployees(keyword)
        );

        model.addAttribute("employee", new Employee());
        model.addAttribute("keyword", keyword);

        return "employees";
    }

    @PostMapping("/employees")
    public String saveEmployee(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model) {

        if (employeeService.emailExists(employee.getEmail())) {
            result.rejectValue(
                    "email",
                    "duplicate",
                    "This email is already registered"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute(
                    "employees",
                    employeeService.getAllEmployees()
            );
            return "employees";
        }

        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/edit")
    public String editEmployee(
            @PathVariable Long id,
            Model model) {

        Employee employee = employeeService
                .getEmployeeById(id)
                .orElseThrow();

        model.addAttribute("employee", employee);
        return "edit-employee";
    }

    @PostMapping("/employees/{id}/update")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result) {

        if (result.hasErrors()) {
            return "edit-employee";
        }

        employee.setId(id);
        employeeService.saveEmployee(employee);

        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }
}