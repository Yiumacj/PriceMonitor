package com.pricemonitorbot.app;

import com.pricemonitorbot.view.ConsoleView;
import com.pricemonitorbot.view.TelegramView;

public class Main {
    /*Todo
    + Add logger
    + Add Test
    + Add interface between api, db
    + Add 
     */
    public static void main(String[] args) {
        //ConsoleView bot = new ConsoleView();
        TelegramView bot = new TelegramView();
        bot.run();
    }
}
