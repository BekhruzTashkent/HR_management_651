package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.entity.TourniquetCard;
import com.pdp.apphrmanagement.payload.TourniquetCardDto;
import com.pdp.apphrmanagement.service.TourniquetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tourniquet")
public class TourniquetController {

    @Autowired
    TourniquetService tourniquetService;


    @PreAuthorize("hasAnyRole('MANAGER','DIRECTOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createCard(@RequestBody TourniquetCardDto tourniquetCardDto){
      return tourniquetService.create(tourniquetCardDto);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/remove/{cardId}")
    public ResponseEntity<?> removeCard(@PathVariable String cardId){
        return tourniquetService.removeCard(cardId);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER','ADMIN')")
    @GetMapping("/edit/{email}")
    public ResponseEntity<?> editCard(@PathVariable String email,@RequestBody TourniquetCardDto tourniquetCardDto){
        return tourniquetService.edit(email,tourniquetCardDto);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    @GetMapping("/activate/{cardId}")
    public ResponseEntity<?> activateCard(@PathVariable String cardId){
        return tourniquetService.activate(cardId);
    }


    @GetMapping("/enter/{cardId}")
    public ResponseEntity<?> enterCard(@PathVariable String cardId){
        return tourniquetService.enter(cardId);
    }

}
