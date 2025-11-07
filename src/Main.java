import main.view.ConsoleView;


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