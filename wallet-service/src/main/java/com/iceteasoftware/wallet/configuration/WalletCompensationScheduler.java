//package com.iceteasoftware.wallet.configuration;
//
//import com.iceteasoftware.wallet.repository.WalletRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class WalletCompensationScheduler {
//
//    private final WalletTransactionRepository walletTransactionRepository;
//    private final WalletRepository walletRepository;
//
//    @Scheduled(fixedDelay = 60000) // Run every 60 seconds
//    public void compensatePendingTransactions() {
//        log.info("Running compensation process for pending transactions...");
//
//        List<WalletTransaction> pendingTransactions = walletTransactionRepository.findByStatus(Status.PENDING);
//        for (WalletTransaction transaction : pendingTransactions) {
//            log.warn("Compensating transaction: {}", transaction.getTransactionId());
//
//            // Rollback wallet balance
//            Wallet wallet = walletRepository.findByWalletCode(transaction.getWalletCode());
//            wallet.setBalance(wallet.getBalance() + transaction.getAmount());
//            walletRepository.save(wallet);
//
//            // Update transaction status to FAILED
//            transaction.setStatus(Status.FAIL);
//            walletTransactionRepository.save(transaction);
//            log.info("Transaction {} compensated successfully.", transaction.getTransactionId());
//        }
//    }
//}
