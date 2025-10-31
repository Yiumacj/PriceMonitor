import test.TestCore;
import test.TestVariations;
import view.ConsoleView;


void main() {
    /*Todo
    + Add logger
    + Add tests
    + Dataclasses refactor
     */
    TestCore.run(TestVariations.ALL);
    ConsoleView bot = new ConsoleView();
    bot.run();
}