package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.service.SalaryService;
import com.pdp.apphrmanagement.payload.SalaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    SalaryService salaryService;

    @PreAuthorize("hasAnyRole('DIRECTOR', 'HR_MANAGER')")
    @PostMapping("/pay")
    public ResponseEntity<?> pay(@Valid @RequestBody SalaryDto dto){
        return salaryService.pay(dto);
    }


    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam String email, @RequestParam String  emailCode){
        return salaryService.confirm(email,emailCode);
    }


    @PostMapping("/reject")
    public ResponseEntity<?> reject(@RequestParam String email, @RequestParam String  emailCode) {
        return salaryService.reject(email,emailCode);
    }



}
