package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.SalaryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryReportRepo extends JpaRepository<SalaryReport,Integer> {
}
