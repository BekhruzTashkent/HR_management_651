package com.pdp.apphrmanagement.service;

import com.pdp.apphrmanagement.entity.EmployeeSalary;
import com.pdp.apphrmanagement.entity.Role;
import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.payload.ApiResponse;
import com.pdp.apphrmanagement.payload.SalaryDto;
import com.pdp.apphrmanagement.repository.EmployeeSalaryRepo;
import com.pdp.apphrmanagement.repository.RoleRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SalaryService {

    @Autowired
    UserRepo userRepo;
    @Autowired
    EmployeeSalaryRepo salaryRepository;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    JavaMailSender javaMailSender;


    public ResponseEntity<?> pay(SalaryDto dto) {

        Role roleEnum = roleRepo.findByRoleEnum(RoleEnum.valueOf(dto.getRole()));
        if (roleEnum == null)
            return ResponseEntity.status(404).body(new ApiResponse(dto.getRole() + " such role does not exist...?", false));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {

            User user = (User) authentication.getPrincipal();
            String verifyingCode = UUID.randomUUID().toString();
            List<User> users = userRepo.findAllByCompanyIdAndEnabledTrueAndRoles(user.getCompany().getId(), dto.getRole());
            for (User user1 : users) {
                EmployeeSalary employeeSalary = new EmployeeSalary();
                employeeSalary.setSalary(user1.getSalary());
                employeeSalary.setUser(user1);
                employeeSalary.setMonth(dto.getMonth());
                employeeSalary.setYear(LocalDate.now().getYear());
                employeeSalary.setVerifyingCode(verifyingCode);
                salaryRepository.save(employeeSalary);
            }
            //todo: Create class than can transaction employee salary
            sendEmailForSalary(user, users, user.getCompany().getId(), verifyingCode, dto.getMonth());
            return ResponseEntity.ok(new ApiResponse("Information are sent to director to confirm", true));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Error with authentication", false));

    }


    private void sendEmailForSalary(User from, List<User> employees, Integer directorId, String verifyingCode, String month) {
        Optional<User> optionalDirector = userRepo.findCompanyDirectorById(directorId);
        if (optionalDirector.isPresent()) {
            User director = optionalDirector.get();
            director.setEmailCode(verifyingCode);
            User savedDirector = userRepo.save(director);
            String confirmLink = "http://localhost:8080/api/salary/confirm?email=" + savedDirector.getEmail() + "&emailCode=" +
                    savedDirector.getEmailCode();
            String rejectLink = "http://localhost:8080/api/salary/reject?email=" + savedDirector.getEmail() + "&emailCode=" +
                    savedDirector.getEmailCode();

            String startHtml = "<table border=\"1px\" cellspacing=\"0px\" cellpadding=\"1px\">\n" +
                    "    <tr>\n" +
                    "        <th>Firstname</th>\n" +
                    "        <th>Lastname</th>\n" +
                    "        <th width=\"90px\">Salary</th>\n" +
                    "    </tr>\n";
            String body = "";
            //Send all employee_name + salary as table to director confirm or reject
            for (User employee : employees) {
                body += "    <tr>\n" +
                        "        <td>" + employee.getFirstName() + "</td>\n" +
                        "        <td>" + employee.getLastName() + "</td>\n" +
                        "        <td>" + employee.getSalary() + "</td>\n" +
                        "    </tr>\n";
            }
            startHtml += body + "</table>\n" +
                    "<form method=\"post\" action=" + rejectLink + "> " +
                    "<button style=\"padding: 5px 10px; background-color: red; margin-top: 5px; color: white \">Reject</button></form>"
                    + "<form method=\"post\" action=" + confirmLink + "> " +
                    "<button style=\"padding: 5px 10px; background-color: #24d024; margin-top: 5px; color: white \">Confirm</button></form>";
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setSubject("Confirm salary for " + month);
                helper.setFrom(from.getEmail());
                helper.setTo(savedDirector.getEmail());
                helper.setText(startHtml, true);
                javaMailSender.send(message);
            } catch (Exception ignored) {
            }
        }
    }


    public ResponseEntity<?> confirm(String email, String emailCode) {
        Optional<User> optionalDirector = userRepo.findByEmailAndEmailCode(email, emailCode);
        if (!optionalDirector.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Email and EmailCode not matched or not found", false));
        User director = optionalDirector.get();
        if (userRepo.isDirector(email)) {

            List<EmployeeSalary> salaries = salaryRepository.findAllByVerifyingCode(emailCode);
            for (EmployeeSalary salary : salaries) {
                salary.setStatus(true);
                salaryRepository.save(salary);
            }

            director.setEmailCode(null);
            userRepo.save(director);
            return ResponseEntity.ok(new ApiResponse<>("All salaries confirmed", true));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You are not director", true));
    }

    public ResponseEntity<?> reject(String email, String emailCode) {
        Optional<User> optionalDirector = userRepo.findByEmailAndEmailCode(email, emailCode);
        if (!optionalDirector.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Email and EmailCode not matched or not found", false));
        User director = optionalDirector.get();

        if (userRepo.isDirector(director.getEmail())) {
            List<EmployeeSalary> salaries = salaryRepository.findAllByVerifyingCode(emailCode);
            salaryRepository.deleteAll(salaries);
            director.setEmailCode(null);
            userRepo.save(director);
            return ResponseEntity.ok(new ApiResponse("All salaries are rejected", true));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You are not director", true));
    }
}
