package com.pdp.apphrmanagement.payload;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalaryDto {

    @NotNull
    private String month;

    @NotNull
    private String role;
}
