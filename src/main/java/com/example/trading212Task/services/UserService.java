package com.example.trading212Task.services;

import com.example.trading212Task.config.UserSession;
import com.example.trading212Task.dtos.UserLoginDTO;
import com.example.trading212Task.dtos.UserRegisterDTO;
import com.example.trading212Task.pojos.User;
import com.example.trading212Task.repositories.AccountDao;
import com.example.trading212Task.repositories.HoldingsDao;
import com.example.trading212Task.repositories.TransactionsDao;
import com.example.trading212Task.repositories.UserDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final double DEFAULT_USER_BALANCE=10000.0;
    private final UserDao userDao;

    private final UserSession userSession;

    private final PasswordEncoder passwordEncoder;

    private final AccountDao accountDao;
    private final HoldingsDao holdingsDao;
    private final TransactionsDao transactionsDao;

    public UserService(UserDao userDao, UserSession userSession, PasswordEncoder passwordEncoder, AccountDao accountDao, HoldingsDao holdingsDao, TransactionsDao transactionsDao) {
        this.userDao = userDao;
        this.userSession = userSession;
        this.passwordEncoder = passwordEncoder;
        this.accountDao = accountDao;
        this.holdingsDao = holdingsDao;
        this.transactionsDao = transactionsDao;
    }

    public boolean register(UserRegisterDTO data) {
        if (userDao.userExists(data.getUsername(), data.getEmail())) {
            return false;
        }

        User user = new User(data.getUsername(), data.getFirstName(), data.getLastName(), data.getEmail(), data.getPassword());

        return userDao.saveUser(user);
    }

    public boolean login(UserLoginDTO data) {
        User user = userDao.findByUsername(data.getUsername());

        if (user == null) {
            return false;
        }

        boolean passMatch = passwordEncoder.matches(data.getPassword(), user.getPassword());

        if (!passMatch) {
            return false;
        }

        userSession.login(user.getId(), user.getUsername());
        return true;
    }

    public void processSellOrder(UserSession userSession, String symbol, double cryptoQuantity, double pricePerCoin) {
        Long userId = userSession.getUserId();

        double amountHeld = holdingsDao.getUserHoldingAmount(userId, symbol);

        if (amountHeld <= 0) {
            throw new IllegalArgumentException("You do not hold any " + symbol + " to sell.");
        }
        if (cryptoQuantity > amountHeld) {
            throw new IllegalArgumentException("You only hold " + amountHeld + " " + symbol + ", cannot sell " + cryptoQuantity + ".");
        }

        if (amountHeld - cryptoQuantity == 0) {
            holdingsDao.deleteUserHolding(userId, symbol);
        } else {
            holdingsDao.decreaseHolding(userId, symbol, cryptoQuantity);
        }

        double totalValue = pricePerCoin * cryptoQuantity;
        accountDao.increaseBalance(userId, totalValue);

        transactionsDao.insertSellTransaction(symbol, "SELL", cryptoQuantity, pricePerCoin, totalValue);
    }

    public void resetAccount(){
        Long userId = userSession.getUserId();

        holdingsDao.deleteAllHoldingsForUser(userId);

        transactionsDao.deleteAllTransactionsForUser(userId);

        accountDao.setBalance(userId, DEFAULT_USER_BALANCE);

        System.out.println("User account " + userId + " successfully reset.");
    }
}
