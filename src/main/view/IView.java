package main.view;

import java.util.ArrayList;

public interface IView {
    void showMessage(ArrayList<String>msg);
    void showWarning(ArrayList<String>msg);
    void showError(ArrayList<String>msg);
    void run();
}
