package com.pricemonitorbot.presenter;

import com.pricemonitorbot.interfaces.service.ISteamApi;
import com.pricemonitorbot.interfaces.view.IView;
import java.io.IOException;
import com.pricemonitorbot.model.DataBaseModel;
import com.pricemonitorbot.model.dataclasses.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.pricemonitorbot.service.SteamApi;
import java.util.ArrayList;

public class Presenter {
    private final DataBaseModel steamDB;
    private final ISteamApi steamApi;
    private IView view;

    public Presenter(){
        String[] dbcfg;
        try {
            dbcfg = Files.readAllLines(Paths.get("C:\\CFG_OOP\\configDB.txt")).get(0).split(";"); // TODO: Сделать нормально
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        steamDB = new DataBaseModel(dbcfg[0], dbcfg[1], dbcfg[2]);
        steamApi = new SteamApi();
    }

    //Constructor for testing
    public Presenter(DataBaseModel param_steamDB, ISteamApi param_steamApi) {
        steamDB = param_steamDB;
        steamApi = param_steamApi;
    }

    public void setView(IView view) {
        this.view = view;
    }

    private void cmdAdd(String link){
        AddQueryStatus query = addGame(link);
        ArrayList<String> msg = new ArrayList<>();
        if (query == AddQueryStatus.OK){
            msg.add("Игра успешно добавлена!");
            view.showMessage(msg);
        }
        else if (query == AddQueryStatus.ALREADY_EXISTS) {
            msg.add("Эта игра уже отслеживается.");
            view.showError(msg);
        }
        else {
            msg.add("Не удалось добавить игру. Проверьте корректность ссылки.");
            view.showError(msg);
        }
    }

    private void cmdCheck(){
        ArrayList<String>msg = new ArrayList<>();
        msg.add("Not released yet.");
        view.showMessage(msg);
    }

    private void cmdGet(String link){
        int appId = parseAppId(link);
        ArrayList<String>msg = new ArrayList<>();

        if (appId == 0) {
            msg.add("Не удалось проверить игру. Проверьте корректность ссылки.");
            view.showError(msg);
            return;
        }

        AppInfo oldAppInfo = steamDB.getById(AppInfo.class, appId);

        if (oldAppInfo == null) {
            msg.add("Приложение с таким id не отслеживается");
            view.showError(msg);
            return;
        }

        AppInfo newAppInfo = steamApi.getGameInfo(appId, "RU");

        steamDB.updateByItem(newAppInfo);

        PriceInfo oldPriceInfo = oldAppInfo.getPriceInfo();
        PriceInfo newPriceInfo = newAppInfo.getPriceInfo();
        int oldPrice = (int) oldPriceInfo.getFinalPrice();
        int newPrice = (int) newPriceInfo.getFinalPrice();
        int priceDiff = newPrice - oldPrice;

        msg.add("Текущая цена товара составляет " +
                newPrice + " " +
                newPriceInfo.getCurrency());
        if (priceDiff == 0) {
            msg.add("Цена товара не изменилась");
        }
        else if (priceDiff < 0) {
            msg.add("Цена товара уменьшилась на " +
                    (-priceDiff) + " " +
                    newPriceInfo.getCurrency());
        }
        else {
            msg.add("Цена товара увеличилась на " +
                    priceDiff + " " +
                    newPriceInfo.getCurrency());
        }

        view.showMessage(msg);
    }

    private void cmdHelp(){
        ArrayList<String>msg = new ArrayList<>();
        msg.add("Привет! Я - бот, мониторящий цены на игры в Steam");
        msg.add("Вот список моих команд:");
        msg.add("/add <ссылка> - отправь мне ссылку на страницу игры, и я начну следить за её ценой");
        msg.add("/check - Расскажу тебе обо всех изменениях цен");
        msg.add("/get <ссылка> - Cкажу тебе текущую цену товара");
        view.showMessage(msg);
    }

    public void feedCommand(String[] command){
        switch (command[0]){
            case "/add" -> cmdAdd(command.length > 1 ? command[1] : "");
            case "/check" -> cmdCheck();
            case "/start", "/help" -> cmdHelp();
            case "/get" -> cmdGet(command.length > 1 ? command[1] : "");
            default -> {
                ArrayList<String> msg = new ArrayList<>();
                msg.add("Моя Твоя не понимать");
                view.showError(msg);
            }
        }
    }

    private AddQueryStatus addGame(String link) {
        int appId = parseAppId(link);

        AppInfo info = steamApi.getGameInfo(appId, "RU");
        if (info == null) {
            return AddQueryStatus.INVALID_LINK;
        }
        if (!steamDB.addByItem(info)){
            return AddQueryStatus.ALREADY_EXISTS;
        }
        return AddQueryStatus.OK;
    }

    private int parseAppId(String link) {
        if (!link.startsWith("https://store.steampowered.com/app/")) {
            return 0;
        }
        try {
            return Integer.parseInt(link.split("/")[4]);
        }
        catch (Exception exc) {
            return 0;
        }
    }
}
