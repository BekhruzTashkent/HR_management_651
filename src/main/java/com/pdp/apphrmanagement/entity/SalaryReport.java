package com.pdp.apphrmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SalaryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private UUID changedBy;

    @Column(nullable = false)
    private UUID changedWhose;

    private Double lastSalary;

    private Double newSalary;

    private Double distinction;

    @CreationTimestamp
    private Date changedAt;


}
