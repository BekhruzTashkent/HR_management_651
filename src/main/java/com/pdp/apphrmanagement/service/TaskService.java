package com.pdp.apphrmanagement.service;

import com.pdp.apphrmanagement.entity.Task;
import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.payload.ApiResponse;
import com.pdp.apphrmanagement.payload.TaskDto;
import com.pdp.apphrmanagement.repository.TaskRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TaskService {

    @Autowired
    UserRepo userRepo;
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    JavaMailSender javaMailSender;

    public ResponseEntity<?> managerTask(TaskDto taskDto) {
        Optional<User> optionalUser = userRepo.findByEmail(taskDto.getAttachedEmployeeEmail());
        if (!optionalUser.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Such worker not found", false));

        User user = optionalUser.get();
        if (userRepo.isManager(user.getEmail())) {

            Task task = new Task();
            task.setName(taskDto.getName());
            task.setComment(taskDto.getComment());
            task.setStatus(1);
            task.setAttachedEmployee(user);
            task.setDeadline(Timestamp.valueOf(Date.valueOf(taskDto.getDeadline()) + "00:00:00"));
            task.setTaskCode(UUID.randomUUID());

            Task savedTask = taskRepo.save(task);
            sendTask(user, savedTask);
            return ResponseEntity.ok(new ApiResponse("Task created successfully!", true));
        }
        return ResponseEntity.ok(new ApiResponse(user.getEmail() + " is not manager", false));
    }


    public ResponseEntity<?> workerTask(TaskDto taskDto) {
        Optional<User> optionalUser = userRepo.findByEmail(taskDto.getAttachedEmployeeEmail());
        if (!optionalUser.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Such worker not found", false));

        User user = optionalUser.get();
        if (userRepo.isWorker(user.getEmail())) {

            Task task = new Task();
            task.setName(taskDto.getName());
            task.setComment(taskDto.getComment());
            task.setStatus(1);
            task.setAttachedEmployee(user);
            task.setDeadline(Timestamp.valueOf(Date.valueOf(taskDto.getDeadline()) + "00:00:00"));
            task.setTaskCode(UUID.randomUUID());

            Task savedTask = taskRepo.save(task);
            sendTask(user, savedTask);
            return ResponseEntity.ok(new ApiResponse("Task created successfully!", true));
        }
        return ResponseEntity.ok(new ApiResponse(user.getEmail() + " is not worker", false));

    }


    public boolean sendTask(User to, Task savedTask) {


        String link = "http://localhost:8080/api/task/confirm?email=" + to.getEmail() + "&taskCode=" + savedTask.getTaskCode();
        String body = "<form action=" + link + " method=\"post\">\n" +
                "<p>" + savedTask.getComment() + "</p>" +
                "<p>Task code=" + savedTask.getTaskCode() + "</p>" +
                "<button style=\"padding: 5px 10px; background-color: #24d024; margin-top: 5px; color: white \">Submit</button>\n" +
                "</form>";

        try {
            Optional<User> optionalUser = userRepo.findById(savedTask.getCreatedBy());
            User from = optionalUser.get();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject(savedTask.getName());
            helper.setFrom(from.getEmail());
            helper.setTo(to.getEmail());
            helper.setText(body, true);
            javaMailSender.send(message);
            to.setTaskCode(savedTask.getTaskCode().toString());
            userRepo.save(to);
            return true;
        } catch (Exception ignored) {
            return false;
        }

    }


    public ResponseEntity<?> confirm(String email, String taskCode) {
        Optional<User> optionalUser = userRepo.findByEmailAndTaskCode(email, taskCode);
        if (!optionalUser.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Email or code isn't correct", false));
        Optional<Task> optionalTask = taskRepo.findByTaskCode(taskCode);
        if (!optionalTask.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Code isn't correct", false));
        Task task = optionalTask.get();
        User user = optionalUser.get();
        task.setStatus(2);
        user.setTaskCode(null);
        taskRepo.save(task);
        userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Task confirmed", true));

    }


    public ResponseEntity<?> completeTask(String taskCode) {
        Optional<Task> optionalTask = taskRepo.findByTaskCode(taskCode);
        if (!optionalTask.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Task not found", false));
        Task task = optionalTask.get();
        if (task.getDeadline().before(new Date(System.currentTimeMillis())))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Submission deadline", false));
        if (task.getStatus() == 1)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Task has been not confirmed", false));
        if (task.getStatus() == 3)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Task has been already completed", false));


        task.setCompletedAtTheTime(true);
        task.setStatus(3);
        task.setCompletedAt(Timestamp.valueOf(LocalDateTime.now()));

        sendForComplete(task);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Complete confirmed!", true));

    }

    public boolean sendForComplete(Task task){
        String body = "<p>Task: " + task.getName() + "</p>" +
                "<p>Description: " + task.getComment() + "</p>" +
                "<p>Completed: " + task.getCompletedAt() + "</p>";
        try {
            User from = userRepo.getById(task.getCreatedBy());
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject("Task completed");
            helper.setFrom(from.getEmail());
            helper.setTo(userRepo.getById(task.getCreatedBy()).getEmail());
            helper.setText(body, true);
            javaMailSender.send(message);
            return true;
        } catch (Exception ignored) {
         return false;
        }
    }

    public ResponseEntity<?> delete(String taskCode) {
        Optional<Task> optionalTask = taskRepo.findByTaskCode(taskCode);
        if(!optionalTask.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Task not found",false));
        taskRepo.delete(optionalTask.get());
         return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>("Task deleted!",true));
    }


}
