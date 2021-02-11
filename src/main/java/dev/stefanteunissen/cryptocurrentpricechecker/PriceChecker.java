package dev.stefanteunissen.cryptocurrentpricechecker;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class PriceChecker {
    private final OkHttpClient client;
    private final ArrayList<Position> positions;
    private BigDecimal totalWorth;

    public PriceChecker() {
        this.client = new OkHttpClient();
        this.positions = new ArrayList<>();
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
                String provider = splitLine[0];
                String market = splitLine[1];
                BigDecimal owned = new BigDecimal(splitLine[2]);
                positions.add(new Position(provider, market, owned));
            }
        }
    }

    public void getPrices() {
        try {
            String returnString = this.checkPrice();
            JSONArray obj = new JSONArray(returnString);
            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);
                for (Position position : positions) {
                    if (position.getMarket().equals(jObject.getString("market"))) {
                        BigDecimal price = BigDecimal.valueOf(jObject.getDouble("price"));
                        position.setPrice(price);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void calculateWorth() {
        this.totalWorth = new BigDecimal(0);
        for (Position pos : positions) {
            BigDecimal worth = pos.getOwned().multiply(pos.getPrice());
            pos.setWorth(worth);
            this.totalWorth = totalWorth.add(worth);
        }
    }

    public void print() {
        String lineFormat = "%-10s%-20s%-20s%s%n";
        System.out.printf(lineFormat, "Market", "Price", "Owned", "Value");
        for (Position pos : positions) {
            System.out.printf(lineFormat, pos.getMarket(), pos.getPrice(), pos.getOwned(), pos.getWorth());
        }
        System.out.printf(lineFormat, "", "", "", "_____________");
        System.out.printf(lineFormat, "bitvavo", "", "", this.totalWorth);
    }

    public String checkPrice() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.bitvavo.com/v2/ticker/price")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }

    private String configLocation() {
        String dir = System.getProperty("user.home") + File.separatorChar;
        if (System.getProperty("os.name").contains("Windows"))
            return dir + "My Documents";
        return dir + ".config" + File.separatorChar + "cryptocurrentpricechecker.conf";
    }
}
