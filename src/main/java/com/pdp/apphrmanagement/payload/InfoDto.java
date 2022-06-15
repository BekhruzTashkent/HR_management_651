package com.pdp.apphrmanagement.payload;

import com.pdp.apphrmanagement.entity.Task;
import com.pdp.apphrmanagement.entity.TourniquetHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InfoDto {
    private String firstName;
    private String lastName;
    private String email;
    private List<Task> tasks;
    private List<TourniquetHistory> histories;
}
