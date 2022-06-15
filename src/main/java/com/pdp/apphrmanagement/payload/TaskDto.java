package com.pdp.apphrmanagement.payload;

import com.pdp.apphrmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.security.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    @NotNull
    private String name;

    @NotNull
    private String comment;

    @NotNull
    private String deadline;

    @NotNull
    private String attachedEmployeeEmail;

    private Integer status = 1; // 1 -> new;   2 -> working...;    3 -> completed ;    0-> rejected

}
