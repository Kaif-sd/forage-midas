package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Balance getUserBalanceService(Long userId) {
        Optional<UserRecord> userRecord = userRepository.findById(userId);
        if(userRecord.isPresent()){
            return new Balance(userRecord.get().getBalance());
        }else{
            return new Balance(0);
        }
    }
}
