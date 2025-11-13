package java.presenter;

import interfaces.service.ISteamApi;
import interfaces.view.IView;
import model.DataBaseModel;
import model.DataClasses.AppInfo;
import model.DataClasses.PriceInfo;
import presenter.Presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PresenterGetCommandTest {

    DataBaseModel db;
    ISteamApi api;
    IView view;
    Presenter presenter;

    private static final String VALID_LINK = "https://store.steampowered.com/app/620/Portal_2/";

    @BeforeEach
    void setUp() {
        db = mock(DataBaseModel.class);
        api = mock(ISteamApi.class);
        view = mock(IView.class);
        presenter = new Presenter(db, api);
        presenter.setView(view);
    }

    @Test
    void invalidLink_shouldShowError() {
        presenter.feedCommand(new String[]{"/get", "https://example.com/not-steam"});
        ArgumentCaptor<ArrayList<String>> cap = ArgumentCaptor.forClass(ArrayList.class);
        verify(view).showError(cap.capture());
        assertTrue(cap.getValue().getFirst().contains("Не удалось проверить игру"));
    }

    @Test
    void notTracked_shouldShowError() {
        int appId = 620;
        when(db.getById(AppInfo.class, appId)).thenReturn(null);

        presenter.feedCommand(new String[]{"/get", VALID_LINK});

        ArgumentCaptor<ArrayList<String>> cap = ArgumentCaptor.forClass(ArrayList.class);
        verify(view).showError(cap.capture());
        assertTrue(cap.getValue().getFirst().contains("Приложение с таким id не отслеживается"));
    }

    @Test
    void priceUnchanged_shouldSayNotChanged() {
        int appId = 620;

        PriceInfo oldPrice = mock(PriceInfo.class);
        when(oldPrice.getFinalPrice()).thenReturn(100.0);
        when(oldPrice.getCurrency()).thenReturn("RUB");

        AppInfo oldApp = mock(AppInfo.class);
        when(oldApp.getPriceInfo()).thenReturn(oldPrice);

        PriceInfo newPrice = mock(PriceInfo.class);
        when(newPrice.getFinalPrice()).thenReturn(100.0);
        when(newPrice.getCurrency()).thenReturn("RUB");

        AppInfo newApp = mock(AppInfo.class);
        when(newApp.getPriceInfo()).thenReturn(newPrice);

        when(db.getById(AppInfo.class, appId)).thenReturn(oldApp);
        when(api.getGameInfo(appId, "RU")).thenReturn(newApp);

        presenter.feedCommand(new String[]{"/get", VALID_LINK});

        ArgumentCaptor<ArrayList<String>> cap = ArgumentCaptor.forClass(ArrayList.class);
        verify(view).showMessage(cap.capture());
        var msg = String.join("\n", cap.getValue());
        assertTrue(msg.contains("Текущая цена товара составляет 100 RUB"));
        assertTrue(msg.contains("Цена товара не изменилась"));
        verify(db).updateByItem(newApp);
    }

    @Test
    void priceDecreased_shouldSayDecreasedBy() {
        int appId = 620;

        PriceInfo oldPrice = mock(PriceInfo.class);
        when(oldPrice.getFinalPrice()).thenReturn(150.0);
        when(oldPrice.getCurrency()).thenReturn("RUB");

        AppInfo oldApp = mock(AppInfo.class);
        when(oldApp.getPriceInfo()).thenReturn(oldPrice);

        PriceInfo newPrice = mock(PriceInfo.class);
        when(newPrice.getFinalPrice()).thenReturn(120.0);
        when(newPrice.getCurrency()).thenReturn("RUB");

        AppInfo newApp = mock(AppInfo.class);
        when(newApp.getPriceInfo()).thenReturn(newPrice);

        when(db.getById(AppInfo.class, appId)).thenReturn(oldApp);
        when(api.getGameInfo(appId, "RU")).thenReturn(newApp);

        presenter.feedCommand(new String[]{"/get", VALID_LINK});

        ArgumentCaptor<ArrayList<String>> cap = ArgumentCaptor.forClass(ArrayList.class);
        verify(view).showMessage(cap.capture());
        var msg = String.join("\n", cap.getValue());
        assertTrue(msg.contains("Текущая цена товара составляет 120 RUB"));
        assertTrue(msg.contains("Цена товара уменьшилась на 30 RUB"));
        verify(db).updateByItem(newApp);
    }

    @Test
    void priceIncreased_shouldSayIncreasedBy() {
        int appId = 620;

        PriceInfo oldPrice = mock(PriceInfo.class);
        when(oldPrice.getFinalPrice()).thenReturn(100.0);
        when(oldPrice.getCurrency()).thenReturn("RUB");

        AppInfo oldApp = mock(AppInfo.class);
        when(oldApp.getPriceInfo()).thenReturn(oldPrice);

        PriceInfo newPrice = mock(PriceInfo.class);
        when(newPrice.getFinalPrice()).thenReturn(130.0);
        when(newPrice.getCurrency()).thenReturn("RUB");

        AppInfo newApp = mock(AppInfo.class);
        when(newApp.getPriceInfo()).thenReturn(newPrice);

        when(db.getById(AppInfo.class, appId)).thenReturn(oldApp);
        when(api.getGameInfo(appId, "RU")).thenReturn(newApp);

        presenter.feedCommand(new String[]{"/get", VALID_LINK});

        ArgumentCaptor<ArrayList<String>> cap = ArgumentCaptor.forClass(ArrayList.class);
        verify(view).showMessage(cap.capture());
        var msg = String.join("\n", cap.getValue());
        assertTrue(msg.contains("Текущая цена товара составляет 130 RUB"));
        assertTrue(msg.contains("Цена товара увеличилась на 30 RUB"));
        verify(db).updateByItem(newApp);
    }
}
