package java.presenter;

import java.interfaces.service.ISteamApi;
import java.interfaces.view.IView;
import java.io.IOException;
import java.model.DataBaseModel;
import java.model.DataClasses.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.service.SteamApi;
import java.util.ArrayList;

public class Presenter {
    private final DataBaseModel steamDB;
    private final ISteamApi steamApi;
    private IView view;
    public Presenter(){
        String[] dbcfg;
        try {
            dbcfg = Files.readAllLines(Paths.get("C:\\CFG_OOP\\configDB.txt")).getFirst().split(";");
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
        int priceDiff = (int) oldPriceInfo.getFinalPrice() - (int) newPriceInfo.getFinalPrice();

        msg.add("Текущая цена товара составляет " +
                (int) newPriceInfo.getFinalPrice() + " " +
                newPriceInfo.getCurrency());
        if (priceDiff == 0) {
            msg.add("Цена товара не изменилась");
        }
        else if (priceDiff < 0) {
            msg.add("Цена товара уменьшилась на " +
                    Integer.toString(priceDiff) + " " +
                    newPriceInfo.getCurrency());
        }
        else {
            msg.add("Цена товара увеличилась на " +
                    Integer.toString(-priceDiff) + " " +
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
            case "/add" -> cmdAdd(command[1]);
            case "/check" -> cmdCheck();
            case "/start", "/help" -> cmdHelp();
            case "/get" -> cmdGet(command[1]);
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
        /*
         * Если ink - корректная ссылка на приложение в стиме,
         * вернётся id этого приложения,
         * иначе вернётся 0.
         * Так как приложения с таким id в стиме не существует,
         * при запросе к бд получим null.
         */
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