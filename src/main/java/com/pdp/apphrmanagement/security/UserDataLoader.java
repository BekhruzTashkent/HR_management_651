package com.pdp.apphrmanagement.security;

import com.pdp.apphrmanagement.entity.Company;
import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import com.pdp.apphrmanagement.repository.CompanyRepo;
import com.pdp.apphrmanagement.repository.RoleRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Log
@Component
public class UserDataLoader implements CommandLineRunner {

    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    CompanyRepo companyRepo;
    @Autowired
    JavaMailSender javaMailSender;
    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
private void loadUserData() {
log.info("In loadUserData class");
    if (userRepo.count() == 1) {
//        roleRepo.save(new Role(1,RoleEnum.ROLE_DIRECTOR));
//        roleRepo.save(new Role(2,RoleEnum.ROLE_MANAGER));
//        roleRepo.save(new Role(3,RoleEnum.ROLE_ADMIN));
//        roleRepo.save(new Role(4,RoleEnum.ROLE_EMPLOYEE));


        userRepo.deleteAll();
        Company company=new Company();
        company.setName("Google");

        Company save = companyRepo.save(company);
        userRepo.deleteByEmail("jr2003mit@gmail.com");
        User user= new User();
        user.setCompany(save);
        user.setSalary(5000d);
        user.setFirstName("Mr.Javohir");
        user.setLastName("Rajabov");
        user.setEmail("jr2003mit@gmail.com");
        user.setPassword(passwordEncoder.encode("123dr"));
        user.setEnabled(true);
        user.setRoles(Collections.singleton(roleRepo.findByRoleEnum(RoleEnum.ROLE_DIRECTOR)));
        userRepo.save(user);

    }
    log.info("Count of Users :"+userRepo.count());
}
}
