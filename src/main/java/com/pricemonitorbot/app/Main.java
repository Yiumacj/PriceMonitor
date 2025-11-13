package com.pricemonitorbot.app;

import com.pricemonitorbot.view.ConsoleView;

public class Main {
    /*Todo
    + Add logger
    + Add Test
    + Add interface between api, db
    + Add 
     */
    public static void main(String[] args) {
        ConsoleView bot = new ConsoleView();
        bot.run();
    }
}
