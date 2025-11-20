package com.pricemonitorbot.view;

import com.pricemonitorbot.interfaces.view.IView;
import com.pricemonitorbot.presenter.Presenter;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;


public class TelegramView implements IView, LongPollingUpdateConsumer {

    private final Presenter presenter;
    private final String token;
    private final String username;
    private final TelegramClient client;

    private Long currentChatId;

    // ===== Возможность подключения кнопок =====
    private Function<Long, ReplyKeyboard> replyKeyboardSupplier;
    private BiFunction<Long, String, InlineKeyboardMarkup> inlineKeyboardSupplier;
    private boolean attachKeyboardToAllMessages = false;

    public TelegramView() {
        this(
                "",
                ""
        );
    }

    public TelegramView(String token, String username) {
        this.token = Objects.requireNonNull(token, "token is null");
        this.username = Objects.requireNonNull(username, "username is null");

        this.client = new OkHttpTelegramClient(token);

        this.presenter = new Presenter();
        this.presenter.setView(this);

        this.inlineKeyboardSupplier = (chatId, firstLine) -> defaultInlineKeyboard();
    }

    public String getBotUsername() {
        return username;
    }

    public String getBotToken() {
        return token;
    }

    // =================================================================
    // LongPollingUpdateConsumer
    // =================================================================

    @Override
    public void consume(List<Update> updates) {
        if (updates == null) return;
        for (Update update : updates) {
            handleUpdate(update);
        }
    }

    private void handleUpdate(Update update) {
        if (update == null) return;

        if (update.hasCallbackQuery()) {
            CallbackQuery cb = update.getCallbackQuery();
            if (cb.getMessage() != null) {
                this.currentChatId = cb.getMessage().getChatId();
            }
            handleCallback(cb);
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        this.currentChatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        if ("/menu".equalsIgnoreCase(text)) {
            showMenu();
            return;
        }

        String[] cmd = toPresenterCommand(text);
        if (cmd == null) {
            ArrayList<String> err = new ArrayList<>();
            err.add("Не понял команду. Отправь /help или /menu");
            showError(err);
            return;
        }

        String verb = cmd[0];
        if (("/add".equals(verb) || "/get".equals(verb)) &&
                (cmd.length < 2 || cmd[1].isBlank())) {
            ArrayList<String> err = new ArrayList<>();
            err.add("Команда требует ссылку: " + verb + " <ссылка>");
            showError(err);
            return;
        }

        try {
            presenter.feedCommand(cmd);
        } catch (Exception e) {
            ArrayList<String> err = new ArrayList<>();
            err.add("Внутренняя ошибка: " + e.getMessage());
            showError(err);
        }
    }

    // =================================================================
    // IView
    // =================================================================

    @Override
    public void showMessage(ArrayList<String> lines) {
        sendLines(lines, attachKeyboardToAllMessages);
    }

    @Override
    public void showWarning(ArrayList<String> msg) {
        sendLines(msg, attachKeyboardToAllMessages);
    }

    @Override
    public void showError(ArrayList<String> lines) {
        ArrayList<String> decorated = new ArrayList<>(lines.size() + 1);
        decorated.add("❗ Ошибка");
        decorated.addAll(lines);
        sendLines(decorated, attachKeyboardToAllMessages);
    }


    @Override
    public void run() {
        try {
            TelegramBotsLongPollingApplication botsApp =
                    new TelegramBotsLongPollingApplication();

            botsApp.registerBot(token, this);

            System.out.println("Bot started. Username: " + username);

            Thread.currentThread().join();
        } catch (TelegramApiException e) {
            System.err.println("Ошибка регистрации бота: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =================================================================
    // Кнопки / меню
    // =================================================================

    public void showMenu() {
        if (currentChatId == null) return;
        SendMessage msg = new SendMessage(currentChatId.toString(), "Выберите действие:");
        ReplyKeyboard markup = resolveReplyMarkup(Arrays.asList("Выберите действие:"));
        if (markup != null) {
            msg.setReplyMarkup(markup);
        }
        safeExecute(msg);
    }

    public void setAttachKeyboardToAllMessages(boolean attach) {
        this.attachKeyboardToAllMessages = attach;
    }

    public void setReplyKeyboardSupplier(Function<Long, ReplyKeyboard> supplier) {
        this.replyKeyboardSupplier = supplier;
    }

    public void setInlineKeyboardSupplier(BiFunction<Long, String, InlineKeyboardMarkup> supplier) {
        this.inlineKeyboardSupplier = supplier;
    }

    // =================================================================
    // Helpers
    // =================================================================

    private void sendLines(List<String> lines, boolean attachKeyboard) {
        if (currentChatId == null || lines == null || lines.isEmpty()) return;
        String text = String.join("\n", lines);
        SendMessage msg = new SendMessage(currentChatId.toString(), text);
        if (attachKeyboard) {
            ReplyKeyboard markup = resolveReplyMarkup(lines);
            if (markup != null) msg.setReplyMarkup(markup);
        }
        safeExecute(msg);
    }

    private ReplyKeyboard resolveReplyMarkup(List<String> lines) {
        ReplyKeyboard markup = null;
        String firstLine = (lines != null && !lines.isEmpty()) ? lines.get(0) : "";
        if (inlineKeyboardSupplier != null) {
            try {
                markup = inlineKeyboardSupplier.apply(currentChatId, firstLine);
            } catch (Exception ignored) {}
        }
        if (markup == null && replyKeyboardSupplier != null) {
            try {
                markup = replyKeyboardSupplier.apply(currentChatId);
            } catch (Exception ignored) {}
        }
        return markup;
    }

    private void safeExecute(SendMessage msg) {
        try {
            client.execute(msg);
        } catch (TelegramApiException e) {
            System.err.println("Telegram send failed: " + e.getMessage());
        }
    }

    /**
     * Преобразует текст сообщения Telegram в формат, ожидаемый Presenter#feedCommand:
     * [0] — команда, [1] — опциональный параметр (ссылка)
     */
    private String[] toPresenterCommand(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String trimmed = raw.trim();
        String[] parts = trimmed.split("\\s+", 2);
        String verb = parts[0].toLowerCase();

        switch (verb) {
            case "/start":
            case "/help":
            case "/check":
                return new String[]{verb};
            case "/add":
            case "/get":
                if (parts.length == 2) {
                    return new String[]{verb, parts[1].trim()};
                } else {
                    return new String[]{verb, ""};
                }
            default:
                if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                    return new String[]{"/get", trimmed};
                }
                return null;
        }
    }

    private void handleCallback(CallbackQuery cb) {
        String data = cb.getData();
        if (data == null || data.isBlank()) return;

        String cmdLine = data.trim();
        String[] cmd = toPresenterCommand(cmdLine);
        if (cmd != null) {
            try {
                presenter.feedCommand(cmd);
            } catch (Exception e) {
                showError(new ArrayList<>(List.of("Внутренняя ошибка: " + e.getMessage())));
            }
        } else {
            showError(new ArrayList<>(List.of("Некорректная команда в кнопке")));
        }
    }


    public static InlineKeyboardMarkup defaultInlineKeyboard() {
        InlineKeyboardButton bCheck = btn("Проверить", "/check");
        InlineKeyboardButton bHelp  = btn("Помощь", "/help");
        InlineKeyboardButton bStart = btn("Старт", "/start");

        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(bCheck);
        row1.add(bHelp);

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(bStart);

        return InlineKeyboardMarkup.builder()
                .keyboard(Arrays.asList(row1, row2))
                .build();
    }

    public static ReplyKeyboardMarkup sampleReplyKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("/check");
        row1.add("/help");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/menu");

        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .keyboard(Arrays.asList(row1, row2))
                .build();
    }

    private static InlineKeyboardButton btn(String text, String data) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(data)
                .build();
    }
}
