package com.example.trading212Task.controllers;

import com.example.trading212Task.config.UserSession;
import com.example.trading212Task.repositories.AccountDao;
import com.example.trading212Task.services.AccountService;
import com.example.trading212Task.services.HoldingService;
import com.example.trading212Task.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    private final UserSession userSession;

    private final AccountService accountService;

    private final HoldingService holdingService;


    public HomeController(UserSession userSession, AccountService accountService, HoldingService holdingService) {
        this.userSession = userSession;
        this.accountService = accountService;
        this.holdingService = holdingService;
    }

    @GetMapping("/home")
    public String getHome(Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        String username = userSession.getUsername();
        double balance = accountService.getUserBalance();

        model.addAttribute("username", username);
        model.addAttribute("balance", balance);
        return "Home";
    }

    @PostMapping("/buy/{symbol}")
    @ResponseBody
    public ResponseEntity<String> buyCrypto(@PathVariable String symbol,
                                            @RequestParam double cryptoQuantity,
                                            @RequestParam double pricePerCoin) {
        if (!userSession.isLoggedIn()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in.");
        }
        try {
            holdingService.processBuyOrder(symbol, cryptoQuantity, pricePerCoin);
            return ResponseEntity.ok("Buy order successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Buy order failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

}
