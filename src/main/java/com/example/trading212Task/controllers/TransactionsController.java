package com.example.trading212Task.controllers;

import com.example.trading212Task.config.UserSession;
import com.example.trading212Task.pojos.Transaction;
import com.example.trading212Task.repositories.TransactionsDao;
import com.example.trading212Task.services.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TransactionsController {

    private final UserSession userSession;

    private final TransactionService transactionService;

    public TransactionsController(UserSession userSession, TransactionService transactionService) {
        this.userSession = userSession;
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public String getTransactions(Model model) {
        if (!userSession.isLoggedIn()) {
            return "Login";
        }
        List<Transaction> allTransactionsByUserId = transactionService.getAllTransactionsByUserId(userSession.getUserId());
        model.addAttribute("transactions", allTransactionsByUserId);
        model.addAttribute("username", userSession.getUsername());
        return "Transactions";
    }
}
