package presenter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import interfaces.view.IView;
import model.dataBaseModel;
import model.DataClasses.*;
import service.steamApi;

public class presenter {
    private final dataBaseModel steamDB;
    private IView view;
    public presenter(){
        String[] dbcfg;
        try {
            dbcfg = Files.readAllLines(Paths.get("C:\\CFG_OOP\\configDB.txt")).getFirst().split(";");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        steamDB = new dataBaseModel(dbcfg[0], dbcfg[1], dbcfg[2]);
    }

    public void setView(IView view) {
        this.view = view;
    }

    private void cmdAdd(String arg){
        addQueryStatus query =  addGame(arg);
        ArrayList<String> msg = new ArrayList<>();
        if (query == addQueryStatus.OK){
            msg.add("Игра успешно добавлена.");
            view.showMessage(msg);
        }
        else if (query == addQueryStatus.ALREADY_EXISTS) {
            msg.add("Не удалось добавить игру. Она уже существует в базе.");
            view.showError(msg);
        }
        else {
            msg.add("Не удалось добавить игру. Не правильная ссылка.");
            view.showError(msg);
        }
    }

    private void cmdCheck(String arg){

        ArrayList<String>msg = new ArrayList<>();
        appInfo app = steamDB.getById(appInfo.class, Integer.parseInt(arg), "appinfo");
        priceInfo price = app.getPriceInfo();
        msg.add("Текущая цена: "+price.getFinalPrice()+" "+price.getCurrency());
        msg.add("Начальная цена: "+price.getInitialPrice()+" "+price.getCurrency());
        msg.add("Разница цены: "+(price.getInitialPrice()-price.getFinalPrice())+" "+price.getCurrency());
        msg.add("Скидка на "+app.getName()+" составляет "+price.getDiscountPercent() +"%");
        view.showMessage(msg);
    }

    private void cmdGet(String arg){
        ArrayList<String>msg = new ArrayList<>();
        try {
            appInfo appInfo = steamDB.getById(appInfo.class, Integer.parseInt(arg), "appinfo");
            priceInfo priceInfo = appInfo.getPriceInfo();
            msg.add("Текущая цена товара составляет " +
                    (int) priceInfo.getFinalPrice() +
                    priceInfo.getCurrency());
        }
        catch (Exception e){
            msg.add("Товар не найден");
        }
        view.showMessage(msg);
    }

    private void cmdHelp(){
        ArrayList<String>msg = new ArrayList<>();
        msg.add("Привет! Я - бот, мониторящий цены на игры в Steam");
        msg.add("Вот список моих команд:");
        msg.add("/add <ссылка> - отправь мне ссылку на страницу игры, и я начну следить за её ценой");
        msg.add("/check - Расскажу тебе обо всех изменениях цен");
        msg.add("/get - Cкажу тебе текущую цену товара");
        msg.add("/del - Удалю товар из базы");
        view.showMessage(msg);
    }

    private void cmdDelete(String arg){
        ArrayList<String>msg = new ArrayList<>();
        boolean result = steamDB.deleteByItem(steamDB.getById(appInfo.class, Integer.parseInt(arg), "appinfo"), "appinfo");
        if (result) msg.add("Успешно удаленно!");
        else msg.add("Неуспешно");
        view.showMessage(msg);
    }

    public void feedCommand(String[] command){
        switch (command[0]){
            case "/add" -> cmdAdd(command[1]);
            case "/check" -> cmdCheck(command[1]);
            case "/start", "/help" -> cmdHelp();
            case "/get" -> cmdGet(command[1]);
            case "/del" -> cmdDelete(command[1]);
            default -> {
                ArrayList<String> msg = new ArrayList<>();
                msg.add("Моя Твоя не понимать");
                view.showError(msg);
            }
        }
    }
    protected int SteamLinkToAppId(String steamLink){
        steamLink = steamLink.trim();
        int appId;
        try {
            appId = Integer.parseInt(steamLink);
        }
        catch (Exception e){
            String[] words = steamLink.split("/");
            appId = Integer.parseInt(words[4]);
        }
        return appId;
    }

    private addQueryStatus addGame(String link) {
        int appId = this.SteamLinkToAppId(link);

        appInfo info = steamApi.getGameInfo(appId, "RU");
        if (info == null) {
            return addQueryStatus.INVALID_LINK;
        }
        if (!steamDB.addByItem(info, "appinfo")){
            return addQueryStatus.ALREADY_EXISTS;
        }
        return addQueryStatus.OK;
    }
}