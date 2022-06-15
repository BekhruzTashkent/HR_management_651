package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.entity.Role;
import com.pdp.apphrmanagement.entity.SalaryReport;
import com.pdp.apphrmanagement.payload.ApiResponse;
import com.pdp.apphrmanagement.payload.ChangeEmail;
import com.pdp.apphrmanagement.payload.SalaryEditDto;
import com.pdp.apphrmanagement.repository.EmployeeSalaryRepo;
import com.pdp.apphrmanagement.repository.RoleRepo;
import com.pdp.apphrmanagement.repository.TaskRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.service.EmployeeService;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import jdk.internal.instrumentation.Logger;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.JpaQueryCreator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@EnableGlobalMethodSecurity( prePostEnabled = true)
@RequestMapping("/api/employee")
public class EmployeeController {


    @Autowired
    UserRepo userRepo;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    EmployeeSalaryRepo employeeSalaryRepo;


    @GetMapping("/home")
    public ResponseEntity<?> homePage(){
        return ResponseEntity.status(200).body("\n\nWelcome to my HR management app of big tech company\n\n");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getEmployee(){
        return employeeService.getEmployee();
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','MANAGER')")
    @GetMapping("/task/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email,@RequestParam String from,@RequestParam String to){
        return employeeService.info(email,from,to);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','MANAGER')")
    @GetMapping("/info/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email){
        return employeeService.getEmployeeByEmail(email);
    }


    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','MANAGER')")
    @GetMapping("/info/all")
    public ResponseEntity<?> allEmployee(){
        log.info("After @PreAuthorize in allEmployee");
        List list=new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);
        return ResponseEntity.status(200).body(userRepo.findAllByRolesId(list));
    }


    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    @GetMapping("/info/all/manager")
    public ResponseEntity<?> allManager(){
        List list=new ArrayList<>();
        list.add(2);
        return ResponseEntity.status(200).body(userRepo.findAllByRolesId(list));

    }

    @PreAuthorize("hasAnyRole('DIRECTOR')")
    @GetMapping("/info/all/director")
    public ResponseEntity<?> allDirector(){
        List list=new ArrayList<>();
        list.add(1);
        return ResponseEntity.status(200).body(userRepo.findAllByRolesId(list));
    }


    @PreAuthorize("hasAnyRole('DIRECTOR')")
    @GetMapping("/info/all/admin")
    public ResponseEntity<?> allAdmin(){
        List list=new ArrayList<>();
        list.add(3);
        return ResponseEntity.status(200).body(userRepo.findAllByRolesId(list));
    }



    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','MANAGER')")
    @GetMapping("/info/all/worker")
    public ResponseEntity<?> allWorker(){
        List list=new ArrayList<>();
        list.add(4);
        return ResponseEntity.status(200).body(userRepo.findAllByRolesId(list));
    }


    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/edit/salary")
    public ResponseEntity<?> editSalary(@Valid @RequestBody SalaryEditDto salaryEditDto){
        return employeeService.editSalary(salaryEditDto);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/fire/{email}")
    public ResponseEntity<?> fireEmployee(@PathVariable String email){
           return employeeService.fireEmployee(email);
    }


    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/recover/{email}")
    public ResponseEntity<?> recoverEmployee(@PathVariable String email){
        return employeeService.recoverEmployee(email);
    }

    @PostMapping("/edit/email")
    public ResponseEntity<?> editEmail(@RequestBody ChangeEmail emails){
        return employeeService.editEmail(emails);
    }




    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/tasks")
    public ResponseEntity<?> infoTask(){
        return ResponseEntity.ok(taskRepo.findAll());
    }


    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/tasks/{email}")
    public ResponseEntity<?> infoTaskByEmail(@PathVariable String email,@RequestParam String from,@RequestParam String to){
    Timestamp timestamp=new Timestamp(456);

      return  employeeService.infoTask(email,from,to);
    }



    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/salary")
    public ResponseEntity<?> infoSalaries(){
        return ResponseEntity.ok(employeeSalaryRepo.findAll());
    }


    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/salary/{email}")
    public ResponseEntity<?> infoSalaryByEmail(@PathVariable String email,@RequestParam String from,@RequestParam String to){
        return employeeService.infoSalary(email,from,to);
    }
}