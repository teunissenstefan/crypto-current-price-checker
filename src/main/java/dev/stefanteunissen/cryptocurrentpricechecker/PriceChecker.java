package dev.stefanteunissen.cryptocurrentpricechecker;

import dev.stefanteunissen.cryptocurrentpricechecker.providers.BitvavoProvider;
import dev.stefanteunissen.cryptocurrentpricechecker.providers.CoinbaseProvider;
import dev.stefanteunissen.cryptocurrentpricechecker.providers.Provider;
import okhttp3.OkHttpClient;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PriceChecker {
    private final ArrayList<Position> positions;
    private final ArrayList<Provider> providers;
    private final ArrayList<Provider> possibleProviders;
    private BigDecimal totalWorth;

    public PriceChecker() {
        this.positions = new ArrayList<>();
        this.possibleProviders = new ArrayList<>();
        this.possibleProviders.add(new BitvavoProvider());
        this.possibleProviders.add(new CoinbaseProvider());
        this.providers = new ArrayList<>();
    }

    public void readFile() throws IOException {
        this.positions.clear();
        File f = new File(this.configLocation());
        if (!f.exists()) {
            PrintWriter writer = new PrintWriter(this.configLocation());
            writer.println("BITVAVO:BTC-EUR:0.0");
            writer.println("BITVAVO:ETH-EUR:0.0");
            writer.close();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(":");
                String provider = splitLine[0].toLowerCase();
                String market = splitLine[1];
                BigDecimal owned = new BigDecimal(splitLine[2]);
                this.positions.add(new Position(provider, market, owned));
                for (Provider possibleProvider : this.possibleProviders) {
                    if (possibleProvider.getName().equals(provider) && !this.providers.contains(possibleProvider))
                        this.providers.add(possibleProvider);
                }
            }
            this.positions.sort(new PositionNameComparator());
        }
    }

    public void getPrices() {
        this.totalWorth = new BigDecimal(0);
        for (Provider provider : this.providers) {
            provider.getPrices(this.positions);
        }
    }

    public void calculateWorths() {
        for (Provider provider : this.providers) {
            totalWorth = totalWorth.add(provider.calculateWorth(this.positions));
        }
    }

    public void print() {
        for (Provider provider : this.providers) {
            provider.printProvider(this.positions);
        }
        String lineFormat = "%-10s%-20s%-20s%s%n";
        System.out.printf(lineFormat, "", "", "", "_____________");
        System.out.printf(lineFormat, "", "", "Total worth:", this.totalWorth);
    }

    private String configLocation() {
        String dir = System.getProperty("user.home") + File.separatorChar;
        if (System.getProperty("os.name").contains("Windows"))
            return dir + "My Documents";
        return dir + ".config" + File.separatorChar + "cryptocurrentpricechecker.conf";
    }
}
