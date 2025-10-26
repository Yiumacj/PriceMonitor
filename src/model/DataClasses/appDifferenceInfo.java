package model.DataClasses;

import interfaces.model.IDataBaseItem;

public class appDifferenceInfo implements IDataBaseItem {
    private final int id;
    private final appInfo currentAppInfo;
    private final appInfo previousAppInfo;
    public appDifferenceInfo(int id, appInfo currentAppInfo, appInfo previousAppInfo) {
        this.id = id;
        this.currentAppInfo = currentAppInfo;
        this.previousAppInfo = previousAppInfo;
    }

    @Override
    public int getId() { return id; }
    public appInfo getCurrentAppInfo() { return currentAppInfo; }
    public appInfo getPreviousAppInfo() { return previousAppInfo; }

}
