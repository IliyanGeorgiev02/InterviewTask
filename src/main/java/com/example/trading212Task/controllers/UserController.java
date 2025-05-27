package com.example.trading212Task.controllers;

import com.example.trading212Task.config.UserSession;
import com.example.trading212Task.dtos.UserLoginDTO;
import com.example.trading212Task.dtos.UserRegisterDTO;
import com.example.trading212Task.pojos.Holding;
import com.example.trading212Task.repositories.AccountDao;
import com.example.trading212Task.repositories.HoldingsDao;
import com.example.trading212Task.repositories.TransactionsDao;
import com.example.trading212Task.services.HoldingService;
import com.example.trading212Task.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private final UserService userService;
    private final UserSession userSession;

    private final HoldingService holdingService;

    public UserController(UserService userService, UserSession userSession, HoldingService holdingService) {
        this.userService = userService;
        this.userSession = userSession;
        this.holdingService = holdingService;
    }

    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @ModelAttribute("registerData")
    public UserRegisterDTO registerDTO() {
        return new UserRegisterDTO();
    }

    @ModelAttribute("loginData")
    public UserLoginDTO loginDTO() {
        return new UserLoginDTO();
    }

    @PostMapping("/register")
    public String doRegister(
            @Valid UserRegisterDTO data,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (userSession.isLoggedIn()) {
            return "redirect:/home";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerData", data);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registerData", bindingResult);

            return "redirect:/register";
        }

        boolean success = userService.register(data);

        if (!success) {
            return "redirect:/register";
        }

        return "redirect:/login";
    }

    @PostMapping("/login")
    public String doLogin(
            @Valid UserLoginDTO data,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (userSession.isLoggedIn()) {
            return "redirect:/home";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginData", data);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.loginData", bindingResult);
            return "redirect:/login";
        }

        boolean success = userService.login(data);

        if (!success) {
            redirectAttributes.addFlashAttribute("loginError", true);

            return "redirect:/login";
        }

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout() {
        if (!userSession.isLoggedIn()) {
            return "redirect:/";
        }

        userSession.logout();

        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        model.addAttribute("username", userSession.getUsername());

        List<Holding> userHoldings = holdingService.getAllUserHoldings(userSession.getUserId());
        model.addAttribute("holdings", userHoldings != null ? userHoldings : new ArrayList<>());
        List<String> symbolsForKraken = new ArrayList<>();
        if (userHoldings != null) {
            symbolsForKraken = userHoldings.stream()
                    .map(Holding::getSymbol)
                    .collect(Collectors.toList());
        }

        model.addAttribute("holdingSymbols", symbolsForKraken);
        return "Profile";
    }

    @PostMapping("/sell/{symbol}")
    public String sellCrypto(@PathVariable String symbol,
                             @RequestParam double cryptoQuantity,
                             @RequestParam double pricePerCoin, Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }
        try {
            userService.processSellOrder(userSession, symbol, cryptoQuantity, pricePerCoin);
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            System.err.println("Sell error: " + e.getMessage());
            return "redirect:/profile";
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during sell: " + e.getMessage());
            return "redirect:/error";
        }
    }


    @PostMapping("/reset-account")
    public String resetAccount() {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }
        try {
            userService.resetAccount();
            return "redirect:/profile";
        } catch (Exception e) {
            System.err.println("Error resetting account for user " + userSession.getUserId() + ": " + e.getMessage());
            return "redirect:/error";
        }
    }
}
