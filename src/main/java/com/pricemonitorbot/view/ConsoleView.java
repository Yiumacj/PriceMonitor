package com.pricemonitorbot.view;

import com.pricemonitorbot.interfaces.view.IView;
import com.pricemonitorbot.presenter.Presenter;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleView implements IView {

    private final Presenter presenter;

    public ConsoleView() {
        presenter = new Presenter();
        presenter.setView(this);
    }

    @Override
    public void showMessage(ArrayList<String> msg) {
        for (String m : msg) {
            System.out.println(m);
        }
    }

    @Override
    public void showWarning(ArrayList<String> msg) {
        System.out.println("Warning.");
        showMessage(msg);
    }

    @Override
    public void showError(ArrayList<String> msg) {
        System.out.println("Error occurred!");
        showMessage(msg);
    }

    @Override
    public void run() {
        presenter.feedCommand(new String[]{"/start"});
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            String[] cmd = input.split("\\s+");
            presenter.feedCommand(cmd);
        }
    }
}
