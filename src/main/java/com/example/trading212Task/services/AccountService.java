package com.example.trading212Task.services;

import com.example.trading212Task.config.UserSession;
import com.example.trading212Task.repositories.AccountDao;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountDao accountDao;

    private final UserService userService;

    private final UserSession userSession;

    public AccountService(AccountDao accountDao, UserService userService, UserSession userSession) {
        this.accountDao = accountDao;
        this.userService = userService;
        this.userSession = userSession;
    }

    public double getUserBalance(){
        return this.accountDao.getUserBalance(this.userSession.getUserId());
    }
}
