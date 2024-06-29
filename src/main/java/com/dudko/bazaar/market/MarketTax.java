package com.dudko.bazaar.market;

import com.google.gson.Gson;

import java.util.List;
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

    public String serialize() {
        return new Gson().toJson(this);
    }

    public static MarketTax deserialize(String serialized) {
        return new Gson().fromJson(serialized, MarketTax.class);
    }

    public static class MarketTaxList {

        private List<MarketTax> marketTaxes;

        public MarketTaxList(List<MarketTax> marketTaxes) {
            this.marketTaxes = marketTaxes;
        }

        public List<MarketTax> getMarketTaxes() {
            return marketTaxes;
        }

        public void setMarketTaxes(List<MarketTax> marketTaxes) {
            this.marketTaxes = marketTaxes;
        }

        public void removeMarketTax(MarketTax marketTax) {
            marketTaxes.remove(marketTax);
        }

        public void addMarketTax(MarketTax marketTax) {
            marketTaxes.add(marketTax);
        }
    }
}


