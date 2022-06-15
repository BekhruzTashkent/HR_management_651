package com.pdp.apphrmanagement.service;

import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import com.pdp.apphrmanagement.payload.ApiResponse;
import com.pdp.apphrmanagement.payload.LoginDto;
import com.pdp.apphrmanagement.payload.RegisterDto;
import com.pdp.apphrmanagement.repository.RoleRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.security.JwtProvider;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Log
@Service
public class AuthService implements UserDetailsService {

    private static final int ATTEMPTS_LIMIT = 3;

    @Autowired(required=true)
    UserRepo userRepo;

    @Autowired(required=true)
    RoleRepo roleRepo;

    @Autowired(required=true)
    PasswordEncoder passwordEncoder;

    @Autowired(required=true)
    AuthenticationManager authenticationManager;

    @Autowired(required=true)
    JwtProvider jwtProvider;

    @Autowired(required=true)
    JavaMailSender javaMailSender;


    /**
     * Add new Manager to server
     *
     * @param registerDto
     * @return class ApiResponse { String message,boolean success}
     */
    public ResponseEntity<?> addManagerByDirector(RegisterDto registerDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();
        log.info("Authentication principal: " + principal.toString());
        log.info("Authentication authorities: " + authentication.getAuthorities().toString());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {

            Optional<User> optionalUser = userRepo.findByEmail(authentication.getName());
            if(!optionalUser.isPresent())
                    return ResponseEntity.status(404).body(new ApiResponse("Email not found", false));

            if (authority.getAuthority().equals("ROLE_DIRECTOR")) {

                Optional<User> byEmail = userRepo.findByEmail(registerDto.getEmail());
                if(byEmail.isPresent())
                    return ResponseEntity.status(401).body(new ApiResponse("Email already in use", false));



                String password = generatePassword();

                //todo generate default password for manager
                User user = new User();
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setCompany(optionalUser.get().getCompany());
                user.setEmail(registerDto.getEmail());
                user.setSalary(registerDto.getSalary());
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(Collections.singleton(roleRepo.findByRoleEnum(RoleEnum.ROLE_MANAGER)));
                user.setEmailCode(UUID.randomUUID().toString());


                //Send message to new Manager inbox for verify
                boolean b = sendEmail(registerDto.getEmail(), "Hi " + registerDto.getFirstName() + " it is your new Director " + authentication.getName() +
                        "\nPlease verify your email via link below\nYour username=" + registerDto.getEmail() + " and password=" + password + "\n\nNote:If you want to change password after login to server you can change only your password" +
                        "  http://localhost:8080/api/auth/verifyEmail?email=" + registerDto.getEmail() + "&emailCode=" + user.getEmailCode(), "Confirm your email");
                if (!b)
                    return ResponseEntity.status(401).body(new ApiResponse("Oops something went wrong with sending email", false));


                //Send message to Director inbox for be sure
                //todo : In JwtFilter class get username into credentials of usernamePasswordAuthenticationToken
                sendEmail((String) authentication.getCredentials(), "A verifyEmail message has been sent to your manager " + user.getEmail(), "Verify email in manager inbox");

                userRepo.save(user);
                return ResponseEntity.status(200).body(new ApiResponse("Mr." + userRepo.findByEmail(authentication.getName()).get().getFirstName() + "  you added manager successfully. Email sent to both your and new manager inbox", true));

            }
        }
        return ResponseEntity.status(401).body(new ApiResponse("You can't add manager without permission", false));
    }


    /**
     * Add new Employee to server
     *
     * @param registerDto
     * @return class ApiResponse { String message,boolean success}
     */
    public ResponseEntity<?> addEmployeeByManager(RegisterDto registerDto) {


        boolean existByEmail = userRepo.existsByEmail(registerDto.getEmail());
        //Check email to be unique
        if (existByEmail)
            return ResponseEntity.status(401).body(new ApiResponse("Email already exist!", false));

//        boolean existsByPassword = userRepo.existsByPassword(registerDto.getPassword());
//        //Check password to be unique
//        if (existsByPassword)
//            return ResponseEntity.status(401).body(new ApiResponse("Password already exist!", false));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Optional<User> optionalUser = userRepo.findByEmail(authentication.getName());
             log.info("Email :"+authentication.getName());
             if(optionalUser.isPresent())
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_MANAGER") || authority.getAuthority().equals("ROLE_DIRECTOR")) {

                String password=generatePassword();
                log.info("Manager has permission");
                User user = new User();
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setEmail(registerDto.getEmail());
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(Collections.singleton(roleRepo.findByRoleEnum(RoleEnum.ROLE_EMPLOYEE)));
                user.setEmailCode(UUID.randomUUID().toString());


                //Send message to new Employee inbox for verify
                boolean b = sendEmail(registerDto.getEmail(), "Hi " + user.getFirstName() + " it is your new Manager " + authentication.getName() +
                        "\nPlease verify your email via link below\nYour username=" + registerDto.getEmail() + " and password=" + password + "\n\nNote:If you want to change password after login to server you can change only your password" +
                        "\n\ngo to http://localhost:8080/api/auth/verifyEmail?email=" + registerDto.getEmail() + "&emailCode=" + user.getEmailCode(), "Confirm your email");
                if (!b)
                    return ResponseEntity.status(401).body(new ApiResponse("Oops something went wrong with sending message to email ", false));

                //Send message to Director inbox for be sure
                //todo : In JwtFilter class get username into credentials of usernamePasswordAuthenticationToken
                sendEmail((String) authentication.getCredentials(), "A verifyEmail message has been sent to new manager " + user.getEmail(), "Verify email in manager inbox");

                userRepo.save(user);

                log.info("New employee saved");
                return ResponseEntity.status(200).body(new ApiResponse("Mr." +  userRepo.findByEmail(authentication.getName()).get().getFirstName() + " \n  Successfully added employee. Email has been sent to both your and new employee inbox", true));

            }
        }
        return ResponseEntity.status(401).body(new ApiResponse("You can't add employee without permission", false));
    }


    public ResponseEntity<?> login(LoginDto loginDto) {

        //todo Use passwordEncode.matches()
        try {

            log.info("Before authenticate User");
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            if (authenticate.isAuthenticated()) {
                String token = jwtProvider.generateToken(loginDto.getUsername(), authenticate.getAuthorities());
                log.info("After authenticate User");
                log.info("Authorities:"+authenticate.getAuthorities());
                 return ResponseEntity.status(201).body(new ApiResponse("Token:", true, token));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Username and Password not matched", false));
//            User user = (User) authenticate.getPrincipal();

        } catch (BadCredentialsException badCredentialsException) {
            log.info("In BadCredentialException catch");
            return ResponseEntity.status(401).body(new ApiResponse("Username and password not found!", false));
        }
    }


    //->->->->->->->->->->->->->->->->->-> Methods ->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->

    /**
     * Send email to user verify code
     *
     * @param emailCode
     * @param email
     * @return class ApiResponse{String message ,boolean success}
     */
    public ResponseEntity<?> verifyEmail(String email, String emailCode) {
        log.info("email="+email+"  ,  emailCode="+emailCode);
        Optional<User> optionalUser = userRepo.findByEmailAndEmailCode(email,emailCode);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setEnabled(true);
            user.setEmailCode(null);
            userRepo.save(user);
            return ResponseEntity.status(401).body(new ApiResponse("Successfully verified", true));
        }
        return ResponseEntity.status(401).body(new ApiResponse("Wrong email or verification code", false));
    }


    /**
     * Send email
     *
     * @param sendingEmail
     * @param text
     * @return boolean
     */
    Boolean sendEmail(String sendingEmail,String text, String subject) {
        try {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Mr.Javakhir");
        message.setTo(sendingEmail);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
        return true;
        } catch (Exception e) {
            log.warning(e.toString());
            return false;
        }
    }


    /**
     * Generate default password for users
     *
     * @return unique password
     */
    String generatePassword() {
        String all = "ASDFGHJKLQWERTYUIOPZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789!@#$%^(){}[]|";
        String pwd = RandomStringUtils.random(15, all);
        for (User user : userRepo.findAll())
            if (passwordEncoder.matches(pwd,user.getPassword())) return generatePassword();
        return pwd;
    }


    /**
     * Override method of UserDetailsService
     *
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional<User> optionalUser = userRepo.findByUsername(email);
//        if(optionalUser.isPresent())
//            return optionalUser.get();
//        throw new UsernameNotFoundException(email+" not found");
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException(email + " not found"));
    }
}
