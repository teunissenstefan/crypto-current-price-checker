package dev.stefanteunissen.cryptocurrentpricechecker;

import java.math.BigDecimal;

public class Position {
    private String provider;
    private String market;
    private BigDecimal price;
    private BigDecimal owned;
    private BigDecimal worth;
    private BigDecimal input;
    private BigDecimal change;
    private boolean isHighlighted = false;

    public Position(String provider, String market, BigDecimal owned, BigDecimal input) {
        this.provider = provider;
        this.market = market;
        this.owned = owned;
        this.input = input;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOwned() {
        return owned;
    }

    public void setOwned(BigDecimal owned) {
        this.owned = owned;
    }

    public BigDecimal getWorth() {
        return worth;
    }

    public void setWorth(BigDecimal worth) {
        this.worth = worth;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public BigDecimal getInput() {
        return input;
    }

    public void setInput(BigDecimal input) {
        this.input = input;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
}
