package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Boolean transactionProcessingService(Long senderId, Long recipientId, BigDecimal amount) {
        try{
            Optional<UserRecord> sender = userVerifier(senderId);
            Optional<UserRecord> recipient = userVerifier(recipientId);
            if(sender.isPresent() && recipient.isPresent()){
                BigDecimal senderBalance = BigDecimal.valueOf(sender.get().getBalance());
                if(senderBalance.compareTo(amount) >= 0){
                    transactionRecordRepository.save(new TransactionRecord(sender.get() , recipient.get() , amount));
                    updatebalance(sender.get(), recipient.get(), amount);
                    return true;
                }else{
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            return  false;
        }

    }

    private Optional<UserRecord> userVerifier(Long userId){
            return userRepository.findById(userId);
    }

    private void updatebalance(UserRecord sender , UserRecord recipient , BigDecimal amount){
        BigDecimal newSenderBalance = BigDecimal.valueOf(sender.getBalance()).subtract(amount);
        BigDecimal newRecipientBalance = BigDecimal.valueOf(recipient.getBalance()).add(amount);

        sender.setBalance(newSenderBalance.floatValue());
        recipient.setBalance(newRecipientBalance.floatValue());

        userRepository.save(sender);
        userRepository.save(recipient);
    }
}
