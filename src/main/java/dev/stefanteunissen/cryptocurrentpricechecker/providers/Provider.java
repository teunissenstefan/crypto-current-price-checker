package dev.stefanteunissen.cryptocurrentpricechecker.providers;

import dev.stefanteunissen.cryptocurrentpricechecker.Position;
import okhttp3.OkHttpClient;

import java.math.BigDecimal;
import java.util.ArrayList;

public abstract class Provider {
    String name;
    final OkHttpClient client;
    BigDecimal totalWorth;

    public Provider(String name) {
        this.client = new OkHttpClient();
        this.name = name;
    }

    public abstract void getPrices(ArrayList<Position> positions);

    public String getName() {
        return this.name;
    }

    public void printProvider(ArrayList<Position> positions) {
        String lineFormat = "%-10s%-20s%-20s%s%n";
        System.out.printf(lineFormat, "Market", "Price", "Owned", "Value");
        for (Position pos : positions) {
            if (pos.getProvider().equals(this.name))
                System.out.printf(lineFormat, pos.getMarket(), pos.getPrice(), pos.getOwned(), pos.getWorth());
        }
        System.out.printf(lineFormat, "", "", "", "_____________");
        System.out.printf(lineFormat, this.name, "", "", this.totalWorth);
        System.out.println();
    }

    public BigDecimal calculateWorth(ArrayList<Position> positions) {
        this.totalWorth = new BigDecimal(0);
        for (Position pos : positions) {
            if (!pos.getProvider().equals(this.name))
                continue;
            BigDecimal worth = pos.getOwned().multiply(pos.getPrice());
            pos.setWorth(worth);
            this.totalWorth = totalWorth.add(worth);
        }
        return this.totalWorth;
    }
}
