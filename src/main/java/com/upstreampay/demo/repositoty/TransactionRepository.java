package com.upstreampay.demo.repositoty;

import com.upstreampay.demo.domain.Transaction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, ObjectId> { }
