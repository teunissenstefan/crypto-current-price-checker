package dev.stefanteunissen.cryptocurrentpricechecker.providers;

import dev.stefanteunissen.cryptocurrentpricechecker.Position;
import dev.stefanteunissen.cryptocurrentpricechecker.PriceChecker;
import okhttp3.OkHttpClient;

import java.math.BigDecimal;
import java.util.ArrayList;

import static dev.stefanteunissen.cryptocurrentpricechecker.App.ROUNDING_MODE;
import static dev.stefanteunissen.cryptocurrentpricechecker.App.ROUNDING_SCALE;
import static dev.stefanteunissen.cryptocurrentpricechecker.Colors.*;

public abstract class Provider {
    String name;
    final OkHttpClient client;
    private BigDecimal totalInput;
    private BigDecimal totalWorth;
    private BigDecimal totalChange;

    public Provider(String name) {
        this.client = new OkHttpClient();
        this.name = name;
    }

    public abstract void getPrices(ArrayList<Position> positions);

    public String getName() {
        return this.name;
    }

    public void printProvider(ArrayList<Position> positions) {
        String lineFormat = "%-10s%-20s%-20s%-20s%-20s%s%n";
        System.out.printf(lineFormat, "Market", "Price", "Owned", "Input", "Value", "Change");
        for (Position pos : positions) {
            if (pos.getProvider().equals(this.name))
                System.out.printf(
                        (pos.isHighlighted() ? ANSI_RED_BACKGROUND : "")+lineFormat+(pos.isHighlighted() ? ANSI_RESET : ""),
                        pos.getMarket(),
                        PriceChecker.printBigDecimal(pos.getPrice()),
                        PriceChecker.printBigDecimal(pos.getOwned()),
                        PriceChecker.printBigDecimal(pos.getInput()),
                        PriceChecker.printBigDecimal(pos.getWorth()),
                        PriceChecker.printBigDecimal(pos.getChange())
                );
        }
        System.out.printf(lineFormat, "", "", "", "", "", "_____________");
        System.out.printf(lineFormat, this.name, "", "", PriceChecker.printBigDecimal(this.getTotalInput()), PriceChecker.printBigDecimal(this.getTotalWorth()), PriceChecker.printBigDecimal(this.getTotalChange()));
        System.out.println();
    }

    public BigDecimal calculateWorth(ArrayList<Position> positions) {
        this.setTotalInput(new BigDecimal(0));
        this.setTotalWorth(new BigDecimal(0));
        this.setTotalChange(new BigDecimal(0));
        for (Position pos : positions) {
            if (!pos.getProvider().equals(this.name))
                continue;
            BigDecimal worth = pos.getOwned().multiply(pos.getPrice());
            pos.setWorth(worth);
            BigDecimal change = pos.getWorth().subtract(pos.getInput());
            pos.setChange(change);
            this.setTotalInput(this.getTotalInput().add(pos.getInput()));
            this.setTotalWorth(this.getTotalWorth().add(worth));
            this.setTotalChange(this.getTotalChange().add(change));
        }
        return this.getTotalWorth();
    }

    public BigDecimal getTotalInput() {
        return totalInput;
    }

    public void setTotalInput(BigDecimal totalInput) {
        this.totalInput = totalInput;
    }

    public BigDecimal getTotalWorth() {
        return totalWorth;
    }

    public void setTotalWorth(BigDecimal totalWorth) {
        this.totalWorth = totalWorth;
    }

    public BigDecimal getTotalChange() {
        return totalChange;
    }

    public void setTotalChange(BigDecimal totalChange) {
        this.totalChange = totalChange;
    }
}
