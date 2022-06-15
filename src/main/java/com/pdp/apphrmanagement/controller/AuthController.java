package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.payload.LoginDto;
import com.pdp.apphrmanagement.payload.RegisterDto;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@RestController
@EnableGlobalMethodSecurity( prePostEnabled = true)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    UserRepo userRepo;
    @Autowired




    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @PreAuthorize("hasRole('ROLE_DIRECTOR')")
    @PostMapping("/manager/add")
    public ResponseEntity<?> addManager(@RequestBody RegisterDto registerDto){
      return authService.addManagerByDirector(registerDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER')")
    @PostMapping("/employee/add")
    public ResponseEntity<?> addEmployee(@RequestBody RegisterDto registerDto){
        return authService.addEmployeeByManager(registerDto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        return authService.login(loginDto);
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<?> enableUser(@RequestParam String email,@RequestParam(required = false) String emailCode){
        return  authService.verifyEmail(email, emailCode);
    }

















//
//    @RestControllerAdvice()
//    public class RestResponseEntityExceptionHandler
//            extends ResponseEntityExceptionHandler {
//
//        @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
//        protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
//            String bodyOfResponse = "You are not allowed to execute";
//            log.info("In RestResponseEntityExceptionHandler");
//            return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
//        }
//    }

}
