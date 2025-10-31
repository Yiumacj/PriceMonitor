package presenter;

import java.util.ArrayList;

import interfaces.view.IView;
import model.dataBaseModel;
import model.dataClasses.*;
import service.steamApi;


public class Presenter {
    private final dataBaseModel dataBaseModel;
    private IView view;
    public Presenter(){
        dataBaseModel = new dataBaseModel();
    }

    public void setView(IView view) {
        this.view = view;
    }

    private void cmdAdd(String link){
        addQueryStatus query = addGame(link);
        ArrayList<String> msg = new ArrayList<>();
        if (query == addQueryStatus.OK){
            msg.add("Игра успешно добавлена!");
            view.showMessage(msg);
        }
        else if (query == addQueryStatus.ALREADY_EXISTS) {
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

        appInfo oldAppInfo = dataBaseModel.getById(appId);

        if (oldAppInfo == null) {
            msg.add("Приложение с таким id не отслеживается");
            view.showError(msg);
            return;
        }

        appInfo newAppInfo = steamApi.getGameInfo(appId, "RU");

        dataBaseModel.update(newAppInfo);

        ariceInfo oldPriceInfo = oldAppInfo.getPriceInfo();
        ariceInfo newPriceInfo = newAppInfo.getPriceInfo();
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

    private addQueryStatus addGame(String link) {
        int appId = parseAppId(link);

        appInfo info = steamApi.getGameInfo(appId, "RU");
        if (info == null) {
            return addQueryStatus.INVALID_LINK;
        }
        if (!dataBaseModel.add(info)){
            return addQueryStatus.ALREADY_EXISTS;
        }
        return addQueryStatus.OK;
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