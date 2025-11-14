import view.ConsoleView;
import view.TelegramView;


void main() {
    /*Todo
    + Add logger
    + Add Test
    + Add interface between api, db
    + Add 
     */
    //ConsoleView bot = new ConsoleView();
    TelegramView bot = new TelegramView();
    bot.run();
}