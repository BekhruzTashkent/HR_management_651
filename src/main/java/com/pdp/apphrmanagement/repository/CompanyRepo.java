package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.Company;
import com.pdp.apphrmanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends JpaRepository<Company,Integer> {
}
