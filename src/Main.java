import test.testCore;
import test.testVariations;
import view.ConsoleView;


void main() {
    /*Todo
    + Add logger
     */
    testCore.run(testVariations.ALL);
    ConsoleView bot = new ConsoleView();
    bot.run();
}