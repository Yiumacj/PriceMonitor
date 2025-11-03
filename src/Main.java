import test.testCore;
import test.testVariations;
import view.ConsoleView;


void main() {
    /*Todo
    + Add logger
    + Add Test
    + Add interface between api, db
    + Add 
     */
    testCore.run(testVariations.ALL);
    ConsoleView bot = new ConsoleView();
    bot.run();
}