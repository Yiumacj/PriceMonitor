package view;

import java.util.ArrayList;
import java.util.Scanner;

import interfaces.view.IView;
import presenter.Presenter;

public class ConsoleView implements IView {

    private final Presenter presenter;

    public ConsoleView() {
        presenter = new Presenter();
        presenter.setView(this);
    }

    @Override
    public void showMessage(ArrayList<String> msg) {
        for (String m : msg) {
            IO.println(m);
        }
    }

    @Override
    public void showWarning(ArrayList<String> msg) {
        IO.println("Warning.");
        showMessage(msg);
    }

    @Override
    public void showError(ArrayList<String> msg) {
        IO.println("Error occurred!");
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