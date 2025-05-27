package com.example.trading212Task.services;

import com.example.trading212Task.config.UserSession;
import com.example.trading212Task.pojos.Holding;
import com.example.trading212Task.repositories.AccountDao;
import com.example.trading212Task.repositories.HoldingsDao;
import com.example.trading212Task.repositories.TransactionsDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HoldingService {

    private final HoldingsDao holdingsDao;
    private final UserSession userSession;
    private final UserService userService;

    private final TransactionsDao transactionsDao;
    private final AccountDao accountDao;

    public HoldingService(HoldingsDao holdingsDao, UserSession userSession, UserService userService, TransactionsDao transactionsDao, AccountDao accountDao) {
        this.holdingsDao = holdingsDao;
        this.userSession = userSession;
        this.userService = userService;
        this.transactionsDao = transactionsDao;
        this.accountDao = accountDao;
    }

    public double getUserHoldingsAmount(Long userId, String symbol) {
        return this.holdingsDao.getUserHoldingAmount(userId, symbol);
    }

    public boolean deleteUserHolding(Long userId, String symbol) {
        return this.holdingsDao.deleteUserHolding(userId, symbol);
    }

    public List<Holding> getAllUserHoldings(Long userId) {
        return this.holdingsDao.getUserHoldings(userId);
    }

    public boolean holdingExists(Long userId, String symbol) {
        return this.holdingsDao.holdingExists(userId, symbol);
    }

    public void increaseHolding(Long userId, String symbol, double quantity) {
        this.holdingsDao.increaseHolding(userId, symbol, quantity);
    }

    public void insertHolding(Long userId, String symbol, double quantity) {
        this.holdingsDao.insertHolding(userId, symbol, quantity);
    }

    public void deleteAllHoldingsForUser(Long userId) {
        this.holdingsDao.deleteAllHoldingsForUser(userId);
    }

    public void decreaseHolding(Long userId, String symbol, double quantity) {
        this.holdingsDao.decreaseHolding(userId, symbol, quantity);
    }

    public void processBuyOrder(String symbol, double cryptoQuantity, double pricePerCoin) {
        Long userId = userSession.getUserId();
        double balance = accountDao.getUserBalance(userId);
        double totalPriceUSD = cryptoQuantity * pricePerCoin;

        if (totalPriceUSD > balance) {
            throw new IllegalArgumentException("Insufficient funds to complete purchase. You need $" +
                    String.format("%.2f", totalPriceUSD - balance) + " more.");
        }

        if (holdingsDao.holdingExists(userId, symbol)) {
            holdingsDao.increaseHolding(userId, symbol, cryptoQuantity);
        } else {
            holdingsDao.insertHolding(userId, symbol, cryptoQuantity);
        }

        accountDao.decreaseBalance(userId, totalPriceUSD);

        transactionsDao.insertBuyTransaction(symbol, "BUY", cryptoQuantity, pricePerCoin, totalPriceUSD);
    }
}
