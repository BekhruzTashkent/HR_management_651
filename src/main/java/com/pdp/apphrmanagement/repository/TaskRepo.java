package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task,Integer> {

    @Query("select t from Task t where t.completedAt between ?1 and ?2 and t.attachedEmployee.email = ?3")
    List<Task> findAllByCompletedAtBetweenAndAttachedEmployee_Email(Timestamp fromDate, Timestamp toDate, String email);

    List<Task> findAllByCreatedAtBetweenAndStatusAndAttachedEmployee_Email(Timestamp createdAt, Timestamp createdAt2, Integer status, String employee_email);

    @Query("select t from Task t where t.taskCode = ?1")
    Optional<Task> findByTaskCode(String taskCode);
}
