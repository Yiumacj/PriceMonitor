package com.pricemonitorbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pricemonitorbot.model.dataclasses.OzonProductInfo;
import com.pricemonitorbot.model.dataclasses.PriceInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class OzonApi {

    private static final String COMPOSER_URL =
            "https://www.ozon.ru/api/composer-api.bx/page/json/v2?url=";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OzonApi() {
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Получает информацию о товаре по обычной ссылке на Ozon
     * и возвращает OzonProduct с вложенным PriceInfo.
     *
     * @param productUrl например: https://www.ozon.ru/product/...-1173897628/
     */
    public OzonProductInfo getProduct(String productUrl)
            throws IOException, InterruptedException {

        // 1. Берём только path из ссылки
        URI uri = URI.create(productUrl);
        String path = uri.getPath(); // /product/...-1173897628/ или /product/...-1173897628

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        // 2. Собираем URL для composer-api
        String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
        String apiUrl = COMPOSER_URL + encodedPath;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .header("User-Agent", "Mozilla/5.0 (compatible; PriceMonitorBot/1.0)")
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ozon returned status " +
                    response.statusCode() + ": " + response.body());
        }

        String body = response.body();

        // 3. Парсим JSON-ответ
        JsonNode root = objectMapper.readTree(body);

        JsonNode seo = root.path("seo");
        if (seo.isMissingNode()) {
            throw new RuntimeException("No 'seo' block in Ozon response");
        }

        JsonNode scripts = seo.path("script");
        if (!scripts.isArray() || scripts.isEmpty()) {
            throw new RuntimeException("No 'seo.script' array in Ozon response");
        }

        String innerHtml = scripts.get(0).path("innerHTML").asText();
        if (innerHtml == null || innerHtml.isEmpty()) {
            throw new RuntimeException("Empty seo.script[0].innerHTML");
        }

        JsonNode ld = objectMapper.readTree(innerHtml);

        String name = ld.path("name").asText(null);
        JsonNode offers = ld.path("offers");
        String priceStr = offers.path("price").asText(null);
        String currency = offers.path("priceCurrency").asText(null);
        String sku = ld.path("sku").asText(null);

        if (name == null || priceStr == null) {
            throw new RuntimeException("Cannot find product name or price in JSON-LD");
        }

        BigDecimal price = new BigDecimal(priceStr);

        // 4. Собираем PriceInfo и OzonProduct под формат хранения

        PriceInfo priceInfo = new PriceInfo(
                price.doubleValue(),  // finalPrice
                price.doubleValue(),  // initialPrice (пока считаем, что скидки нет)
                0,                    // discountPercent
                currency              // валюту Ozon обычно отдаёт "RUB"
        );



        return new OzonProductInfo(
                productUrl,
                name,
                sku,
                priceInfo);
    }
}
