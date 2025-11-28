package com.pricemonitorbot.app;

import com.pricemonitorbot.config.AppConfig;
import com.pricemonitorbot.interfaces.view.IView;
import com.pricemonitorbot.view.ConsoleView;
import com.pricemonitorbot.view.TelegramView;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        AppConfig appConfig = AppConfig.getInstance();

        List<IView>views = new ArrayList<>() {
            {
                if (appConfig.isConsoleEnabled()) add(new ConsoleView());
                if (appConfig.isTelegramEnabled()) add(new TelegramView());
            }
        };
        List<Thread>viewsThreads = new ArrayList<>();
        for (IView view : views) {
            viewsThreads.add(new Thread(view::run));
        }
        for (Thread view : viewsThreads) {
            view.start();
        }
    }
}
