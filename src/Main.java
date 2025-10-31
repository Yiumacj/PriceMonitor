import test.testCore;
import test.testVariations;
import view.ConsoleView;


void main() {
    /*Todo
    + Add logger
    + Add tests
    + Dataclasses refactor
     */
    testCore.run(testVariations.ALL);
    ConsoleView bot = new ConsoleView();
    bot.run();
}