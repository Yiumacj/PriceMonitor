package presenter;

import java.util.ArrayList;

import interfaces.view.IView;
import model.dataBaseModel;
import model.DataClasses.*;
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

    private void cmdAdd(String arg){
        AddQueryStatus query =  addGame(arg);
        ArrayList<String> msg = new ArrayList<>();
        if (query == AddQueryStatus.OK){
            msg.add("Игра успешно добавлена.");
            view.showMessage(msg);
        }
        else if (query == AddQueryStatus.ALREADY_EXISTS) {
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
        msg.add("Not released yet.");
        view.showMessage(msg);
    }
    private void cmdGet(String arg){
        ArrayList<String>msg = new ArrayList<>();
        PriceInfo priceInfo = dataBaseModel.getById(Integer.parseInt(arg)).getPriceInfo();
        msg.add("Текущая цена товара составляет " +
                (int) priceInfo.getFinalPrice() +
                priceInfo.getCurrency());
        view.showMessage(msg);
    }
    private void cmdHelp(){
        ArrayList<String>msg = new ArrayList<>();
        msg.add("Привет! Я - бот, мониторящий цены на игры в Steam");
        msg.add("Вот список моих команд:");
        msg.add("/add <ссылка> - отправь мне ссылку на страницу игры, и я начну следить за её ценой");
        msg.add("/check - Расскажу тебе обо всех изменениях цен");
        msg.add("/get - Cкажу тебе текущую цену товара");
        view.showMessage(msg);
    }

    public void feedCommand(String[] command){
        switch (command[0]){
            case "/add" -> cmdAdd(command[1]);
            case "/check" -> cmdCheck(command[1]);
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
        String[] words = link.split("/");
        int appId = Integer.parseInt(words[4]);

        AppInfo info = steamApi.getGameInfo(appId, "RU");
        if (info == null) {
            return AddQueryStatus.INVALID_LINK;
        }
        if (!dataBaseModel.add(info)){
            return AddQueryStatus.ALREADY_EXISTS;
        }
        return AddQueryStatus.OK;
    }
}