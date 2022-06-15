package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.EmployeeSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeSalaryRepo extends JpaRepository<EmployeeSalary,Integer> {
    @Query("select e from EmployeeSalary e where e.verifyingCode = ?1")
    List<EmployeeSalary> findAllByVerifyingCode(String emailCode);

    List<EmployeeSalary> findAllByUpdatedAtBetweenAndEmployeeId(Timestamp fromDate, Timestamp toDate, UUID id);
}
