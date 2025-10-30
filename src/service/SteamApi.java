package service;

import model.DataClasses.AppInfo;
import model.DataClasses.PriceInfo;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SteamApi {

    public static AppInfo getGameInfo(int gameId, String region) {
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

            return null;
        }
    }

    private static AppInfo parseJsonResponse(String json, int gameId) {
        try {
            if (!json.contains("\"success\":true")) {
                return null;
            }

            String name = extractValue(json, "\"name\"", 1);
            if (name == null) return null;

            boolean isFree = json.contains("\"is_free\":true");

            String description = extractValue(json, "\"detailed_description\"", 2);

            PriceInfo priceInfo = null;
            if (!isFree && json.contains("\"price_overview\"")) {
                priceInfo = extractPriceInfo(json);
            }

            return new AppInfo(gameId, name, isFree, description, priceInfo);

        } catch (Exception e) {
            return null;
        }
    }

    private static String extractValue(String json, String key, int quoteType) {
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) return null;

        int valueStart = json.indexOf(":", keyIndex) + 1;
        if (quoteType == 1) {
            int quoteStart = json.indexOf("\"", valueStart) + 1;
            int quoteEnd = json.indexOf("\"", quoteStart);
            return json.substring(quoteStart, quoteEnd);
        } else if (quoteType == 2) {
            int quoteStart = json.indexOf("\"", valueStart) + 1;
            int quoteEnd = json.indexOf("\"", quoteStart);
            String value = json.substring(quoteStart, quoteEnd);
            return value.replace("\\n", "\n").replace("\\r", "").replace("\\\"", "\"");
        }
        return json.substring(valueStart, json.indexOf(",", valueStart));
    }

    private static PriceInfo extractPriceInfo(String json) {
        try {
            int priceStart = json.indexOf("\"price_overview\"");
            if (priceStart == -1) return null;

            String finalPriceStr = extractValue(json.substring(priceStart), "\"final\"", 0);
            String initialPriceStr = extractValue(json.substring(priceStart), "\"initial\"", 0);
            String discountStr = extractValue(json.substring(priceStart), "\"discount_percent\"", 0);
            String currency = extractValue(json.substring(priceStart), "\"currency\"", 1);

            int discount = discountStr != null ? Integer.parseInt(discountStr) : 0;
            double initialPrice = initialPriceStr != null ? Double.parseDouble(initialPriceStr) / 100 : -1;
            double finalPrice = finalPriceStr != null ? Double.parseDouble(finalPriceStr) / 100 : -1;

            return new PriceInfo(finalPrice, initialPrice, discount, currency);
        } catch (Exception e) {
            return null;
        }
    }
}
