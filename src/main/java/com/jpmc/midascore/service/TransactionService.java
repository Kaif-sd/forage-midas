package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public Boolean transactionProcessingService(Long senderId, Long recipientId, BigDecimal amount) {
        try {
            Optional<UserRecord> sender = userVerifier(senderId);
            Optional<UserRecord> recipient = userVerifier(recipientId);

            if (sender.isPresent() && recipient.isPresent()) {

                BigDecimal senderBalance = BigDecimal.valueOf(sender.get().getBalance());

                if (senderBalance.compareTo(amount) >= 0) {

                    transactionRecordRepository.save(
                            new TransactionRecord(sender.get(), recipient.get(), amount)
                    );
                    BigDecimal incentiveAmount = getIncentiveAmountFromAPI(senderId, recipientId, amount);
                    updatebalance(sender.get(), recipient.get(), amount, incentiveAmount);

                    return true;
                } else {
                    return false;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Optional<UserRecord> userVerifier(Long userId) {
        return userRepository.findById(userId);
    }

    private void updatebalance(UserRecord sender, UserRecord recipient, BigDecimal amount, BigDecimal incentiveAmount) {

        BigDecimal newSenderBalance = BigDecimal.valueOf(sender.getBalance()).subtract(amount);

        BigDecimal newRecipientBalance = BigDecimal.valueOf(recipient.getBalance()).add(amount);

        if (incentiveAmount != null && incentiveAmount.compareTo(BigDecimal.ZERO) > 0) {
            newRecipientBalance = newRecipientBalance.add(incentiveAmount);
        }

        sender.setBalance(newSenderBalance.floatValue());
        recipient.setBalance(newRecipientBalance.floatValue());

        userRepository.save(sender);
        userRepository.save(recipient);
    }

    private BigDecimal getIncentiveAmountFromAPI(Long senderId, Long recipientId, BigDecimal amount) {
        try {
            Transaction transaction = new Transaction();
            transaction.setSenderId(senderId);
            transaction.setRecipientId(recipientId);
            transaction.setAmount(amount.floatValue());

            Incentive incentive = restTemplate.postForObject(
                    "http://localhost:8080/incentive",
                    transaction,
                    Incentive.class
            );
            return incentive != null ? BigDecimal.valueOf(incentive.getAmount()) : BigDecimal.ZERO;

        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
