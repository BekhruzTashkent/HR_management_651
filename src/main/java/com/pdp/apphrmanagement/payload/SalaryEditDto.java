package com.pdp.apphrmanagement.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryEditDto {

    private String email;
    private Double salary;

}
