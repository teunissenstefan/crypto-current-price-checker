package dev.stefanteunissen.cryptocurrentpricechecker;

import dev.stefanteunissen.cryptocurrentpricechecker.providers.BitvavoProvider;
import dev.stefanteunissen.cryptocurrentpricechecker.providers.CoinbaseProvider;
import dev.stefanteunissen.cryptocurrentpricechecker.providers.Provider;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

import static dev.stefanteunissen.cryptocurrentpricechecker.App.ROUNDING_MODE;
import static dev.stefanteunissen.cryptocurrentpricechecker.App.ROUNDING_SCALE;

public class PriceChecker {
    private final ArrayList<Position> positions;
    private final ArrayList<Provider> providers;
    private final ArrayList<Provider> possibleProviders;
    private BigDecimal totalInput;
    private BigDecimal totalWorth;
    private BigDecimal totalChange;

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
            writer.println("#provider:market:owned:input");
            writer.println("BITVAVO:BTC-EUR:0.0:0.0");
            writer.println("BITVAVO:ETH-EUR:0.0:0.0");
            writer.println("--highlighted--");
            writer.println("BITVAVO:BTC-EUR");
            writer.close();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean atHighlighted = false;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("--highlighted--")){
                    atHighlighted = true;
                    continue;
                }
                if(line.startsWith("#"))
                    continue;
                if(!atHighlighted){
                    String[] splitLine = line.split(":");
                    String provider = splitLine[0].toLowerCase();
                    String market = splitLine[1];
                    BigDecimal owned = new BigDecimal(splitLine[2]);
                    BigDecimal input = new BigDecimal(splitLine[3]);
                    this.positions.add(new Position(provider, market, owned, input));
                    for (Provider possibleProvider : this.possibleProviders) {
                        if (possibleProvider.getName().equals(provider) && !this.providers.contains(possibleProvider))
                            this.providers.add(possibleProvider);
                    }
                }else{
                    String[] splitLine = line.split(":");
                    String provider = splitLine[0].toLowerCase();
                    String market = splitLine[1];
                    for (Position position : this.positions) {
                        if (position.getProvider().equals(provider) && position.getMarket().equals(market))
                            position.setHighlighted(true);
                    }
                }
            }
            this.positions.sort(new PositionNameComparator());
        }
    }

    public void getPrices() {
        this.totalInput = new BigDecimal(0);
        this.totalWorth = new BigDecimal(0);
        this.totalChange = new BigDecimal(0);
        for (Provider provider : this.providers) {
            provider.getPrices(this.positions);
        }
    }

    public void calculateWorths() {
        for (Provider provider : this.providers) {
            totalWorth = totalWorth.add(provider.calculateWorth(this.positions)).setScale(ROUNDING_SCALE, ROUNDING_MODE);
            totalInput = totalInput.add(provider.getTotalInput()).setScale(ROUNDING_SCALE, ROUNDING_MODE);
            totalChange = totalChange.add(provider.getTotalChange()).setScale(ROUNDING_SCALE, ROUNDING_MODE);
        }
    }

    public void print() {
        for (Provider provider : this.providers) {
            provider.printProvider(this.positions);
        }
        String lineFormat = "%-10s%-20s%-20s%-20s%-20s%s%n";
        System.out.printf(lineFormat, "", "", "", "", "", "_____________");
        System.out.printf(lineFormat, "", "", "", "", "Total input:", printBigDecimal(this.totalInput));
        System.out.printf(lineFormat, "", "", "", "", "Total worth:", printBigDecimal(this.totalWorth));
        System.out.printf(lineFormat, "", "", "", "", "Total change:", printBigDecimal(this.totalChange));
    }

    private String configLocation() {
        String dir = System.getProperty("user.home") + File.separatorChar;
        if (System.getProperty("os.name").contains("Windows"))
            return dir + "My Documents";
        return dir + ".config" + File.separatorChar + "cryptocurrentpricechecker.conf";
    }

    public static String printBigDecimal(BigDecimal bigDecimal){
        String returnString = bigDecimal.setScale(ROUNDING_SCALE, ROUNDING_MODE).toString();
        return returnString.startsWith("0E") ? "0" : returnString;
    }
}
