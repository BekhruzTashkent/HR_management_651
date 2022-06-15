package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.payload.TaskDto;
import com.pdp.apphrmanagement.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityListeners;
import java.util.logging.Logger;

@Slf4j
@Controller
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    TaskService taskService;



    @PreAuthorize("hasAnyRole('DIRECTOR')")
    @PostMapping("/manager")
    public ResponseEntity<?> createTaskForManager(@RequestBody TaskDto taskDto){
        return taskService.managerTask(taskDto);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/worker")
    public ResponseEntity<?> createTaskForWorker(@RequestBody TaskDto taskDto){
        return taskService.workerTask(taskDto);
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmTask(@RequestParam String email,@RequestParam String taskCode){
        return taskService.confirm(email,taskCode);
    }

    @GetMapping("/complete/{taskCode}")
    public ResponseEntity<?> completeTask(@PathVariable String taskCode){
    return  taskService.completeTask(taskCode);
    }

    @DeleteMapping("/reject/{taskCode}")
    public ResponseEntity<?> deleteTask(@PathVariable String taskCode){
        return taskService.delete(taskCode);
    }




}
