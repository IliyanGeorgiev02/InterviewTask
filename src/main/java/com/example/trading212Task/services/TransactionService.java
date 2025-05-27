package com.example.trading212Task.services;

import com.example.trading212Task.pojos.Transaction;
import com.example.trading212Task.repositories.TransactionsDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionsDao transactionsDao;


    public TransactionService(TransactionsDao transactionsDao) {
        this.transactionsDao = transactionsDao;
    }

    public List<Transaction> getAllTransactionsByUserId(long userId) {
        return this.transactionsDao.getAllTransactionsByUserId(userId);
    }
}
