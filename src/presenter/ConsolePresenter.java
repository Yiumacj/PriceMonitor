package presenter;

import java.util.ArrayList;

import model.DataBaseModel;
import model.DataClasses.*;
import service.SteamAppInfo;

public class ConsolePresenter {
    private DataBaseModel dataBaseModel;

    public ConsolePresenter(){
        dataBaseModel = new DataBaseModel();
    }

    public Result<AddQueryStatus> addGame(String link) {
        String[] words = link.split("/");
        int appId = Integer.parseInt(words[4]);
        AppInfo info = SteamAppInfo.getGameInfo(appId, "RU");
        PriceInfo priceInfo = info.getPriceInfo();
        double price = priceInfo.getFinalPrice();

        dataBaseModel.add(new ItemInfo(appId, (int)price));
        return new Result<AddQueryStatus>(
                ResultStatus.OK,
                AddQueryStatus.OK
        );
    }

    public Result< ArrayList<ItemCostDifference>> getGamesDiff() {
        //Должен возвращать инфу обо всех играх с изменившейся ценой
        return new Result<ArrayList<ItemCostDifference>>(
                ResultStatus.OK,
                new ArrayList<ItemCostDifference>()
        );
    }

    public  Result<ItemInfo> getGamePrice(String sid){
        return new Result<ItemInfo>(ResultStatus.OK, dataBaseModel.getById(Integer.parseInt(sid)));
    }
}