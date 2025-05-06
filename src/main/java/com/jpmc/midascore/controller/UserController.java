package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/balance")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public BigDecimal getUserBalance(@RequestParam Long userId){
        Optional<UserRecord> userRecord = userRepository.findById(userId);
        return userRecord.map(record -> BigDecimal.valueOf(record.getBalance())).orElse(BigDecimal.ZERO);
    }

}
