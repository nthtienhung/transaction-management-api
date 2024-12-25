package com.transactionservice.configuration.schedule;

import com.transactionservice.configuration.kafka.KafkaProducer;
import com.transactionservice.dto.request.UpdateWalletRequest;
import com.transactionservice.entity.Transaction;
import com.transactionservice.enums.Stage;
import com.transactionservice.enums.Status;
import com.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TransactionCleanupScheduler {
    private final TransactionRepository transactionRepository;
    private final KafkaProducer kafkaProducer;

    @Scheduled(fixedRate = 60000) // Chạy mỗi 1 phút
    public void cleanupIncompleteTransactions() {
        try {
            // Tìm các giao dịch DEDUCTED quá 5 phút
            List<Transaction> incompleteTransactions = transactionRepository
                    .findByUpdatedDateAndStatus(
                            Instant.now().minusSeconds(300),
                            Status.PENDING
                    );
            log.info("Found {} incomplete transactions", Instant.now().minusSeconds(300));
            log.info("Found {} incomplete transactions", incompleteTransactions);

            for (Transaction tx : incompleteTransactions) {
                log.info("Processing incomplete transaction: {}", tx.getTransactionCode());

                if(tx.getStage() == Stage.CREATED){
                    tx.setStatus(Status.FAILED);
                    tx.setErrorMessage("Giao dịch chưa được xác nhận");
                    transactionRepository.save(tx);
                    continue;
                }
                if (tx.getStage() == Stage.DEDUCTED) {
                    try {
                        // Gửi yêu cầu hoàn tiền
                        kafkaProducer.sendMessage("COMPENSATION",
                                buildUpdateWalletRequest(tx)
                        );

                        // Cập nhật trạng thái transaction
                        tx.setStatus(Status.FAILED);
                        tx.setDescription("Giao dịch timeout - đang hoàn tiền");
                        transactionRepository.save(tx);

                    } catch (Exception ex) {
                        log.error("Failed to deducted compensation transaction {}: {}",
                                tx.getTransactionCode(),
                                ex.getMessage()
                        );
                    }
                }
                if (tx.getStage() == Stage.CREDITED) {
                    try {
                        // Gửi yêu cầu hoàn tiền
                        kafkaProducer.sendMessage("COMPENSATION",
                                buildUpdateWalletRequest(tx)
                        );

                        // Cập nhật trạng thái transaction
                        tx.setStatus(Status.FAILED);
                        tx.setDescription("Giao dịch timeout - đang hoàn tiền");
                        transactionRepository.save(tx);

                    } catch (Exception ex) {
                        log.error("Failed to credited compensation transaction {}: {}",
                                tx.getTransactionCode(),
                                ex.getMessage()
                        );
                    }
                }

            }
        } catch (Exception ex) {
            log.error("Error in cleanup job: {}", ex.getMessage());
        }
    }

    private UpdateWalletRequest buildUpdateWalletRequest(Transaction transaction) {
        return UpdateWalletRequest.builder()
                .transactionCode(transaction.getTransactionCode())
                .senderWalletCode(transaction.getSenderWalletCode())
                .recipientWalletCode(transaction.getRecipientWalletCode())
                .stage(transaction.getStage())
                .amount(transaction.getAmount())
                .build();
    }
}