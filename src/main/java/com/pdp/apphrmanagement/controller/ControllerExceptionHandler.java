//package com.pdp.apphrmanagement.controller;
//
//import lombok.extern.java.Log;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import java.util.*;
//
//@Log
//@ControllerAdvice
//public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers,
//                                                                  HttpStatus status,
//                                                                  WebRequest request) {
//        Map<String,Object> body =new LinkedHashMap<>();
//        body.put("timestamp",new Date());
//        body.put("status",status.value());
//
//        List<String> errors=new ArrayList<>();
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errors.add(error.getDefaultMessage());
//        }
//
//        body.put("errors",errors);
//         log.info("ControllerExceptionHandler works");
//         return new ResponseEntity<>(body,headers,status);
//     }
//}
