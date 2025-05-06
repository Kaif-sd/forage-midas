package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TransactionListener {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    UserRepository userRepository;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen1(Transaction transaction) {

        boolean flag = transactionService.transactionProcessingService(
                transaction.getSenderId(),
                transaction.getRecipientId(),
                BigDecimal.valueOf(transaction.getAmount())
        );
        System.out.println("✅ Received Kafka message: " + transaction + " and recipeint balance " + userRepository.findById(transaction.getRecipientId()).get().getBalance() + " with flag :-  "+ flag);  // DEBUG

    }
}
