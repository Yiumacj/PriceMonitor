package java.interfaces.service;

import java.model.DataClasses.AppInfo;

public interface ISteamApi {
    AppInfo getGameInfo(int gameId, String region);
}
