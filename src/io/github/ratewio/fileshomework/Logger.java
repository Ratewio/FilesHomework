package io.github.ratewio.fileshomework;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private final String loggerName;
    private final StringBuilder history;

    public Logger(String loggerName) {
        this.loggerName = loggerName;
        history = new StringBuilder();
    }

    public void log(String message) {
        String logMessage = String.format("[%s]: %s\n", loggerName, message);
        history.append(logMessage);
        System.out.printf(logMessage);
    }

    public void saveToFile(File file) {
        log("saving log to " + file.getPath() + "...");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(history.toString());
            log("log saved to " + file.getPath());
        } catch (IOException e) {
            log("cant save log due exception: " + e);
        }
    }
}
