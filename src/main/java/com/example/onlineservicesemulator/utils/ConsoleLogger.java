package com.example.onlineservicesemulator.utils;

import javafx.scene.control.TextArea;

public class ConsoleLogger {
    private static TextArea textAreaConsole;

    public static void log(String text) {
        textAreaConsole.appendText(text + "\n");
    }

    public static void setTextAreaConsole(TextArea textAreaConsole) {
        ConsoleLogger.textAreaConsole = textAreaConsole;
    }
}
