package com.upstreampay.demo.service;

import com.upstreampay.demo.domain.Transaction;
import com.upstreampay.demo.domain.enums.TransactionStatus;
import com.upstreampay.demo.exception.InvalidInputException;
import com.upstreampay.demo.exception.NotFoundException;
import com.upstreampay.demo.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TransactionService {

    private TransactionRepository transactionRepository;

    public Mono<Transaction> create(Transaction transaction) {
        validateNewTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    public Flux<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Mono<Transaction> update(Transaction transaction) {
        return transactionRepository.findById(transaction.getId())
                .flatMap(original -> {
                    validateExistingTransaction(transaction, original);
                    return transactionRepository.save(transaction);
                })
                .switchIfEmpty(Mono.error(new NotFoundException()));

    }

    private void validateExistingTransaction(Transaction transaction, Transaction original) {
        if (TransactionStatus.CAPTURED.equals(transaction.getStatus()) && !TransactionStatus.AUTHORIZED.equals(original.getStatus())) {
            throw new InvalidInputException("Transaction must be in AUTHORIZED status");
        }
        if (TransactionStatus.CAPTURED.equals(original.getStatus())) {
            throw new InvalidInputException("Transaction status can't be updated");
        }
        if (!original.getOrder().equals(transaction.getOrder()) && !original.getStatus().equals(transaction.getStatus())) {
            throw new InvalidInputException("Can't update order on transaction update");
        }
    }

    private void validateNewTransaction(Transaction transaction) {
        if (!TransactionStatus.NEW.equals(transaction.getStatus())) {
            throw new InvalidInputException("New transaction must be in NEW status");
        }
    }
}
