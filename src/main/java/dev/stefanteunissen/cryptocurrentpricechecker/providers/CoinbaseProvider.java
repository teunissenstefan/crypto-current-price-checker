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

public class CoinbaseProvider extends Provider {
    public CoinbaseProvider() {
        super("coinbase".toLowerCase());
    }

    @Override
    public void getPrices(ArrayList<Position> positions) {
        try {
            for (Position position : positions) {
                if(!position.getProvider().equals(this.name))
                    continue;
                String returnString = this.checkPrice(position.getMarket());
                JSONObject obj = new JSONObject(returnString);
                if(!obj.has("data"))
                    continue;
                JSONObject jObject = obj.getJSONObject("data");
                BigDecimal price = BigDecimal.valueOf(jObject.getDouble("amount"));
                position.setPrice(price);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String checkPrice(String name) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.coinbase.com/v2/prices/"+name+"/spot")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
