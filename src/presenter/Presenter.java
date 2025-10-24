package presenter;

import java.util.ArrayList;

import interfaces.view.IView;
import model.DataBaseModel;
import model.DataClasses.*;
import service.SteamApi;

public class Presenter {
    private DataBaseModel dataBaseModel;
    private IView view;
    public Presenter(){
        dataBaseModel = new DataBaseModel();
    }

    public void setView(IView view) {
        this.view = view;
    }

    public void feedCommand(String[] command){
        boolean result;
        switch (command[0]){
            case "/add" -> {
                AddQueryStatus query =  addGame(command[1]);
                result = (query == AddQueryStatus.OK);
                ArrayList<String> msg = new ArrayList<>();
                if (result) {
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
            case "/check" ->{
                ArrayList<ItemCostDifference> diffs = getGamesDiff();
                ArrayList<String> msg = new ArrayList<>();
                if (diffs.isEmpty()) {
                    msg.add("Не было обнаружено изменение цен.");
                } else {
                    for (ItemCostDifference diff : diffs) {
                        msg.add(diff.toString());
                    }
                }
                view.showMessage(msg);

            }
            case "/start", "/help" ->{
                ArrayList<String>msg = new ArrayList<>();
                msg.add("Привет! Я - бот, мониторящий цены на игры в Steam");
                msg.add("Вот список моих команд:");
                msg.add("/add <ссылка> - отправь мне ссылку на страницу игры, и я начну следить за её ценой");
                msg.add("/check - Расскажу тебе обо всех изменениях цен");
                view.showMessage(msg);
            }
        }
    }

    private AddQueryStatus addGame(String link) {
        String[] words = link.split("/");
        int appId = Integer.parseInt(words[4]);

        AppInfo info = SteamApi.getGameInfo(appId, "RU");
        assert info != null;
        PriceInfo priceInfo = info.getPriceInfo();
        double price = priceInfo.getFinalPrice();

        dataBaseModel.add(new ItemInfo(appId, (int)price));
        return AddQueryStatus.OK;
    }

    private ArrayList<ItemCostDifference> getGamesDiff() {
        return new ArrayList<>();
    }

    private ItemInfo getGamePrice(String sid){
        return dataBaseModel.getById(Integer.parseInt(sid));
    }
}