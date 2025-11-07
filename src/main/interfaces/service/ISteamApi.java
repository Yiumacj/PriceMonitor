package interfaces.service;

import model.DataClasses.AppInfo;

public interface ISteamApi {
    AppInfo getGameInfo(int gameId, String region);
}
