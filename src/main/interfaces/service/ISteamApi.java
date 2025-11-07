package main.interfaces.service;

import main.model.DataClasses.AppInfo;

public interface ISteamApi {
    AppInfo getGameInfo(int gameId, String region);
}
