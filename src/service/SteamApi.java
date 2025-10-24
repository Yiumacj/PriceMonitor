package service;

import model.DataClasses.AppInfo;
import model.DataClasses.PriceInfo;
import model.DataClasses.Result;
import model.DataClasses.ResultStatus;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class SteamApi {

    public static Result<AppInfo> getGameInfo(int gameId, String region) {
        try {
            // Формируем URL запроса
            String urlString = "https://store.steampowered.com/api/appdetails?appids=" + gameId + "&cc=" + region;
            URL url = new URL(urlString);

            // Создаем соединение
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Проверяем статус ответа
            if (connection.getResponseCode() != 200) {
                return null;
            }

            // Читаем ответ
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            connection.disconnect();

            // Парсим JSON ответ
            return new Result<AppInfo>(ResultStatus.OK, Objects.requireNonNull(parseJsonResponse(response.toString(), gameId)).getResult());

        } catch (Exception e) {
            String msg ="Ошибка при получении информации: "+ e.getMessage();
            ArrayList<String> comment = new ArrayList<String>();
            comment.add(msg);
            return new Result<AppInfo>(ResultStatus.ERROR, null, comment);
        }
    }

    private static Result<AppInfo> parseJsonResponse(String json, int gameId) {
        try {
            // Простой парсинг JSON без внешних библиотек
            if (!json.contains("\"success\":true")) {
                return null;
            }

            // Извлекаем название игры
            String name = extractValue(json, "\"name\"", 1).getResult();
            if (name == null) return null;

            // Проверяем бесплатность
            boolean isFree = json.contains("\"is_free\":true");

            // Извлекаем описание
            String description = extractValue(json, "\"detailed_description\"", 2).getResult();

            // Извлекаем цену
            PriceInfo priceInfo = null;
            if (!isFree && json.contains("\"price_overview\"")) {
                priceInfo = extractPriceInfo(json).getResult();
            }

            return new Result<AppInfo>(ResultStatus.OK, new AppInfo(gameId, name, isFree, description, priceInfo));

        } catch (Exception e) {
            String msg = "Ошибка парсинга JSON: "+ e.getMessage();
            ArrayList<String> comment = new ArrayList<String>();
            comment.add(msg);
            return new Result<AppInfo>(ResultStatus.ERROR, null, comment);
        }
    }

    private static Result<String> extractValue(String json, String key, int quoteType) {
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) return null;

        int valueStart = json.indexOf(":", keyIndex) + 1;
        if (quoteType == 1) {
            // Значение в двойных кавычках
            int quoteStart = json.indexOf("\"", valueStart) + 1;
            int quoteEnd = json.indexOf("\"", quoteStart);
            return new Result<String>(ResultStatus.OK, json.substring(quoteStart, quoteEnd));
        } else if (quoteType == 2) {
            // Многострочное значение
            int quoteStart = json.indexOf("\"", valueStart) + 1;
            int quoteEnd = json.indexOf("\"", quoteStart);
            String value = json.substring(quoteStart, quoteEnd);
            return new Result<String>(ResultStatus.OK, value.replace("\\n", "\n").replace("\\r", "").replace("\\\"", "\""));
        }
        return new Result<String>(ResultStatus.OK, json.substring(valueStart, json.indexOf(",", valueStart)));
    }

    private static Result<PriceInfo> extractPriceInfo(String json) {
        try {
            int priceStart = json.indexOf("\"price_overview\"");
            if (priceStart == -1) return null;

            // Извлекаем финальную цену
            String finalPriceStr = extractValue(json.substring(priceStart), "\"final\"", 0).getResult();
            String initialPriceStr = extractValue(json.substring(priceStart), "\"initial\"", 0).getResult();
            String discountStr = extractValue(json.substring(priceStart), "\"discount_percent\"", 0).getResult();
            String currency = extractValue(json.substring(priceStart), "\"currency\"", 1).getResult();

            if (finalPriceStr != null && currency != null) {
                double finalPrice = Double.parseDouble(finalPriceStr) / 100;
                double initialPrice = Double.parseDouble(initialPriceStr) / 100;
                int discount = discountStr != null ? Integer.parseInt(discountStr) : 0;

                return new Result<PriceInfo>(ResultStatus.OK, new PriceInfo(finalPrice, initialPrice, discount, currency));
            }
        } catch (Exception e) {
            String msg = "Ошибка извлечения информации о цене: " + e.getMessage();
            ArrayList<String> comment = new ArrayList<String>();
            comment.add(msg);
            return new Result<PriceInfo>(ResultStatus.ERROR, null, comment);
        }
        return new Result<PriceInfo>(ResultStatus.REJECTED, null);
    }
}
