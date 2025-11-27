package com.pricemonitorbot.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pricemonitorbot.interfaces.service.ISteamApi;
import com.pricemonitorbot.interfaces.view.IView;
import com.pricemonitorbot.model.DataBaseModel;
import com.pricemonitorbot.model.dataclasses.AppInfo;
import com.pricemonitorbot.presenter.Presenter;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class PresenterAddCommandTest {

    DataBaseModel db;
    ISteamApi api;
    IView view;
    Presenter presenter;

    @BeforeEach
    void setUp() {
        db = mock(DataBaseModel.class);
        api = mock(ISteamApi.class);
        view = mock(IView.class);
        presenter = new Presenter(db, api);
        presenter.setView(view);
    }

    @Test
    void add_invalidLink_shouldShowError() {
        when(api.getGameInfo(0, "RU")).thenReturn(null);
        presenter.feedCommand(new String[]{"/add", "https://example.com/not-steam"});
        verify(view, atLeastOnce()).showError(any(ArrayList.class));
        verify(api).getGameInfo(0, "RU");
        verifyNoMoreInteractions(db);
    }

    @Test
    void add_validNewApp_shouldShowOk() {
        int appId = 620;
        AppInfo info = mock(AppInfo.class);

        when(api.getGameInfo(appId, "RU")).thenReturn(info);
        when(db.addByItem(info)).thenReturn(true);

        presenter.feedCommand(new String[]{"/add", "https://store.steampowered.com/app/620/Portal_2/"});

        verify(view, atLeastOnce()).showMessage(any(ArrayList.class));
    }

    @Test
    void add_alreadyExists_shouldShowAlreadyExists() {
        int appId = 620;
        AppInfo info = mock(AppInfo.class);

        when(api.getGameInfo(appId, "RU")).thenReturn(info);
        when(db.addByItem(info)).thenReturn(false);

        presenter.feedCommand(new String[]{"/add", "https://store.steampowered.com/app/620/Portal_2/"});

        verify(view, atLeastOnce()).showError(any(ArrayList.class));
    }
}
