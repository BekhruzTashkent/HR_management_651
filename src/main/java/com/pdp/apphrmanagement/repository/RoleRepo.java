package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.Role;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role,Integer> {
     @Query("select r from Role r where r.roleEnum = ?1")
     Role findByRoleEnum(RoleEnum roleEnum);
}
