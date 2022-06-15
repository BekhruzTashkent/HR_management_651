package com.pdp.apphrmanagement;

import com.pdp.apphrmanagement.repository.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication/*(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(value = "com.pdp.apphrmanagement.repository")*/
public class AppHrManagementApplication {


    public static void main(String[] args) {
        SpringApplication.run(AppHrManagementApplication.class, args);
    }
}
