package com.pdp.apphrmanagement.service;

import com.pdp.apphrmanagement.entity.Company;
import com.pdp.apphrmanagement.entity.TourniquetCard;
import com.pdp.apphrmanagement.entity.TourniquetHistory;
import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.payload.ApiResponse;
import com.pdp.apphrmanagement.payload.TourniquetCardDto;
import com.pdp.apphrmanagement.repository.CompanyRepo;
import com.pdp.apphrmanagement.repository.TourniquetCardRepo;
import com.pdp.apphrmanagement.repository.TourniquetHistoryRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.readers.operation.ResponseMessagesReader;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TourniquetService {

    @Autowired
    UserRepo userRepo;
    @Autowired
    CompanyRepo companyRepo;
    @Autowired
    TourniquetCardRepo tourniquetCardRepo;
    @Autowired
    TourniquetHistoryRepo tourniquetHistoryRepo;

    public ResponseEntity<?> create(TourniquetCardDto tourniquetCardDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User creator = userRepo.findByEmail(email).get();
        if (creator.getCompany().getId() != tourniquetCardDto.getCompanyId())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You can't add to another company without permission", false));

        Optional<User> optionalUser = userRepo.findByEmail(tourniquetCardDto.getUserEmail());
        if (!optionalUser.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Such employee not found", false));

        Optional<Company> optionalCompany = companyRepo.findById(tourniquetCardDto.getCompanyId());
        if (!optionalCompany.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Such company not found", false));

        TourniquetCard card = new TourniquetCard();
        card.setCompany(optionalCompany.get());
        card.setUser(optionalUser.get());

        tourniquetCardRepo.save(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Successfully card created", true));
    }


    public ResponseEntity<?> removeCard(String cardId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User remover = userRepo.findByEmail(email).get();
        Optional<TourniquetCard> optionalTourniquetCard = tourniquetCardRepo.findById(UUID.fromString(cardId));
        if (!optionalTourniquetCard.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Card not found", false));

        TourniquetCard card = optionalTourniquetCard.get();
        if (remover.getCompany().getId() != card.getCompany().getId())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You can't add to another company without permission", false));

        tourniquetCardRepo.delete(card);
        return ResponseEntity.status(HttpStatus.GONE).body(new ApiResponse<>("Card removed successfully", true));
    }


    public ResponseEntity<?> edit(String email, TourniquetCardDto dto) {
        Optional<User> optionalEmployee = userRepo.findByEmail(dto.getUserEmail());
        if (!optionalEmployee.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Employee not found", false));

        Optional<TourniquetCard> optionalTourniquetCard = tourniquetCardRepo.findByUser_EmailAndStatusTrue(email);
        if (optionalTourniquetCard.isPresent()) {
            TourniquetCard card = optionalTourniquetCard.get();
            card.setUser(optionalEmployee.get());
            tourniquetCardRepo.save(card);
            return  ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse("Card updated", true));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Employee not found", false));
    }

    public ResponseEntity<?> activate(String cardId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User activator = userRepo.findByEmail(email).get();
        Optional<TourniquetCard> optionalCard = tourniquetCardRepo.findById(UUID.fromString(cardId));
        if (!optionalCard.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Card not found", false));

        TourniquetCard card = optionalCard.get();
        if (activator.getCompany().getId() != card.getCompany().getId())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You can't activate another company card without permission", false));

        card.setStatus(true);
        card.setExpireAt(new Date(System.currentTimeMillis()+1000L * 60 * 60 * 24 * 365 * 3));
        tourniquetCardRepo.save(card);
        return ResponseEntity.status(200).body(new ApiResponse<>("Card activated", false));

    }


    public ResponseEntity<?> enter(String cardId) {
        Optional<TourniquetCard> optionalCard = tourniquetCardRepo.findById(UUID.fromString(cardId));
        if(!optionalCard.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Card not found",false));

        TourniquetCard card = optionalCard.get();

        if(!card.isStatus())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Card not allowed to enter",false));


        if(card.getExpireAt().getTime()<System.currentTimeMillis())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Card expired,Please activate your card",false));


        TourniquetHistory tourniquetHistory = new TourniquetHistory();
        tourniquetHistory.setEnteredAt(Timestamp.valueOf(LocalDateTime.now()));
        tourniquetHistory.setTourniquetCard(card);
        tourniquetHistoryRepo.save(tourniquetHistory);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Entered", true));

    }


    public ResponseEntity<?> exit(String cardId){
        Optional<TourniquetCard> optionalCard = tourniquetCardRepo.findById(UUID.fromString(cardId));
        if(!optionalCard.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Card not found",false));

        TourniquetCard card = optionalCard.get();

        if(!card.isStatus())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Card not allowed to enter",false));


        if(card.getExpireAt().getTime()<System.currentTimeMillis())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Card expired,Please activate your card",false));


        Optional<TourniquetHistory> optionalTourniquetHistory = tourniquetHistoryRepo.findByTourniquetCardAndExitedAtNull(card);
        if(!optionalTourniquetHistory.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You did not enter via tourniquet ",false));

        TourniquetHistory tourniquetHistory = optionalTourniquetHistory.get();
        tourniquetHistory.setExitedAt(Timestamp.valueOf(LocalDateTime.now()));
        tourniquetHistoryRepo.save(tourniquetHistory);
        return ResponseEntity.status(200).body(new ApiResponse<>("Bye bye",true));
    }



}
