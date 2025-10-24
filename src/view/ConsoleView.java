package view;

import java.util.ArrayList;
import java.util.Scanner;

import model.DataClasses.AddQueryStatus;
import presenter.ConsolePresenter;
import model.DataClasses.ItemCostDifference;

public class ConsoleView {
    // TODO написать бота
    /*
     * /start /help -- выводит инфу о боте и список команд
     * /add {link} -- ссылка на страницу в стиме
     * /check -- изменения цен с последней проверки
     */

    private ConsolePresenter consolePresenter;

    public ConsoleView(){
        consolePresenter = new ConsolePresenter();
    }

    private void help() {
        System.out.println("Привет! Я - бот, мониторящий цены на игры в Steam");
        System.out.println("Вот список моих команд:");
        System.out.println("/add <ссылка> - отправь мне ссылку на страницу игры, и я начну следить за её ценой");
        //System.out.println("/check - Расскажу тебе обо всех изменениях цен");
        System.out.println("/get - Скажу тебе цену игры");
    }

    private void add(String link) {
        AddQueryStatus result = consolePresenter.addGame(link).getResult();
        switch (result) {
            case OK:
                System.out.println("Ссылка успешно добавлена!");
                break;

            case INVALID_LINK:
                System.out.println("Некорректная ссылка, проверьте еще раз");
                break;

            case ALREADY_EXISTS:
                System.out.println("Эта игра уже отслеживается");
                break;
            default:
                break;
        }
    }

    private void get(String sid){
        System.out.println(Integer.toString(consolePresenter.getGamePrice(sid).getResult().price));
    }

    private void check() {
        ArrayList<ItemCostDifference> diffs = consolePresenter.getGamesDiff().getResult();

        if (diffs.isEmpty()) {
            System.out.println("Цены на отслеживаемые игры не изменились");
        }

        for (ItemCostDifference diff : diffs) {
            System.out.println("Изменилась цена на https://store.steampowered.com/app/" + Integer.toString(diff.id));
            if (diff.newPrice < diff.oldPrice) {
                System.out.println("Цена упала на " + Integer.toString(diff.newPrice - diff.oldPrice) + "рублей.");
            }
            else {
                System.out.println("Цена выросла на " + Integer.toString(diff.oldPrice - diff.newPrice) + "рублей.");
            }
            System.out.println("Новая цена: " + Integer.toString(diff.newPrice) + "рублей.");
            System.out.println();
        }
    }

    public void start() {
        Scanner input = new Scanner(System.in);
        this.help();

        while (true) {
            String text = input.nextLine();
            String[] words = text.split("\\s+");

            switch (words[0]) {
                case "/start", "/help":
                    this.help();
                    break;

                case "/add":
                    this.add(words[1]);
                    break;

                case "/check":
                    this.check();
                    break;

                case "/get":
                    this.get(words[1]);
                    break;

                default:
                    System.out.println("Моя твоя не понимать");
                    break;
            }
        }
    }
}