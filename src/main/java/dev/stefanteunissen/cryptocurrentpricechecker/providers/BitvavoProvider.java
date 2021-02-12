package dev.stefanteunissen.cryptocurrentpricechecker.providers;

import dev.stefanteunissen.cryptocurrentpricechecker.Position;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class BitvavoProvider extends Provider {
    public BitvavoProvider() {
        super("bitvavo".toLowerCase());
    }

    @Override
    public void getPrices(ArrayList<Position> positions) {
        try {
            String returnString = this.checkPrice();
            JSONArray obj = new JSONArray(returnString);
            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);
                for (Position position : positions) {
                    if (position.getMarket().equals(jObject.getString("market")) && position.getProvider().equals(this.name)) {
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

    public String checkPrice() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.bitvavo.com/v2/ticker/price")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }


}
