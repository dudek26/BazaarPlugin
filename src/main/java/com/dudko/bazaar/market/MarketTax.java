package com.dudko.bazaar.market;

import java.util.UUID;

@SuppressWarnings("unused")
public class MarketTax {

    private double tax;
    private UUID receiver;


    public MarketTax(UUID receiver, double tax) {
        this.tax = cappedTax(tax);
        this.receiver = receiver;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = cappedTax(tax);
    }

    public UUID getReceiver() {
        return receiver;
    }

    public void setReceiver(UUID receiver) {
        this.receiver = receiver;
    }

    public static double cappedTax(double tax) {
        return Math.min(Math.max(tax, 0.0), 1.0);
    }

    public double calculateTax(double price) {
        return price * tax;
    }
}


