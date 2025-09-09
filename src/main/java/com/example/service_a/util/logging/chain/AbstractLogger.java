package com.example.service_a.util.logging.chain;

import com.example.service_a.util.logging.target.LoggerTarget;
import lombok.Data;


/*
 * Abstract class using string-based log levels for Chain of Responsibility
 */
@Data
public abstract class AbstractLogger {
    protected String level;
    private AbstractLogger nextLevelLogger;

    public void logMessage(String level, String message, LoggerTarget loggerTarget) throws Exception {
        if (this.level.equals(level)) {
            display(level, message, loggerTarget);
        }
        if (nextLevelLogger != null) {
            nextLevelLogger.logMessage(level, message, loggerTarget);
        }
    }

    protected abstract void display(String level, String message, LoggerTarget loggerTarget) throws Exception;
}