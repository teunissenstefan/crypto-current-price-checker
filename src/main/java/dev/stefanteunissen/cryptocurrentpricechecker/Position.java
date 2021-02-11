package dev.stefanteunissen.cryptocurrentpricechecker;

import java.math.BigDecimal;

public class Position {
    private String provider;
    private String market;
    private BigDecimal price;
    private BigDecimal owned;
    private BigDecimal worth;

    public Position(String provider, String market, BigDecimal owned) {
        this.provider = provider;
        this.market = market;
        this.owned = owned;
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
}
