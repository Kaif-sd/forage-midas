package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

//    @Autowired
//    private UserService userService;

    @Autowired
    private UserRepository userRepository;




    @PostMapping("/process")
    public ResponseEntity<Boolean> processTransaction(@RequestBody TransactionRecord transactionRecord){
        Long senderid = transactionRecord.getSender().getId();
        Long recipientId = transactionRecord.getRecipient().getId();
        BigDecimal amount = transactionRecord.getAmount();

        Boolean success = transactionService.transactionProcessingService(senderid , recipientId , amount);
        if(success){
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(false , HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/")
    public String waldorfBalance(){
        try{
            List<UserRecord> userRecord = (List<UserRecord>) userRepository.findAll();
            return "users is :- " + userRecord;
        }catch (Exception e){
            return " error for :- " + e.getMessage();
        }

    }

    @GetMapping("/sample")
    public String sample(){

        return "done";
    }
}
