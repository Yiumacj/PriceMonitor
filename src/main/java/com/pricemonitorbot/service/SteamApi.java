package com.pricemonitorbot.service;

import com.pricemonitorbot.interfaces.service.ISteamApi;
import com.pricemonitorbot.model.dataclasses.AppInfo;
import com.pricemonitorbot.model.dataclasses.PriceInfo;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class SteamApi implements ISteamApi {
    private static final int MOD = (int) 1e9 + 7;

    public AppInfo getGameInfo(int gameId, String region) {
        try {
            String urlString = "https://store.steampowered.com/api/appdetails?appids=" + gameId + "&cc=" + region;
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                return null;
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            connection.disconnect();

            return parseJsonResponse(response.toString(), gameId);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static AppInfo parseJsonResponse(String jsonString, int gameId) {
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONObject gameData = root.getJSONObject(String.valueOf(gameId));

            if (!gameData.getBoolean("success")) {
                return null;
            }

            JSONObject data = gameData.getJSONObject("data");
            String name = data.optString("name", null);
            if (name == null) return null;

            boolean isFree = data.optBoolean("is_free", false);
            String description = data.optString("detailed_description", "");

            PriceInfo priceInfo = null;
            if (!isFree && data.has("price_overview")) {
                priceInfo = extractPriceInfo(data.getJSONObject("price_overview"));
            }

            return new AppInfo(gameId, name, isFree, description, priceInfo);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PriceInfo extractPriceInfo(JSONObject priceJson) {
        try {
            double initialPrice = priceJson.optDouble("initial", -1) / 100.0;
            double finalPrice = priceJson.optDouble("final", -1) / 100.0;
            int discount = priceJson.optInt("discount_percent", 0);
            String currency = priceJson.optString("currency", "USD");

            int id = UUID.randomUUID().hashCode() % MOD;
            return new PriceInfo(id, finalPrice, initialPrice, discount, currency);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
