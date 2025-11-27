package com.pricemonitorbot.interfaces.service;

import com.pricemonitorbot.model.dataclasses.AppInfo;

public interface ISteamApi {
    AppInfo getGameInfo(int gameId, String region);
}
