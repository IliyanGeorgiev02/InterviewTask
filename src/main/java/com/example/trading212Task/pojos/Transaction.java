package com.example.trading212Task.pojos;

import java.time.LocalDateTime;

public class Transaction {
    private String symbol;
    private String type;
    private int amount;
    private double price;
    private LocalDateTime dateTime;
    private double totalPrice;

    public Transaction(String symbol, String type, int amount, double price, LocalDateTime dateTime, double totalPrice) {
        this.symbol = symbol;
        this.type = type;
        this.amount = amount;
        this.price = price;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
    }

    public Transaction() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
