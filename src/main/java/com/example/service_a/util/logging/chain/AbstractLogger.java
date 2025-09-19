/**
 * Abstract class for logging using Chain of Responsibility pattern with log levels.
 *
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.util.logging.chain;

import com.example.service_a.component.LoggerTarget;
import lombok.Data;


@Data
public abstract class AbstractLogger {
    protected String level;
    private AbstractLogger nextLevelLogger;

    /**
     * Logs a message if the level matches or passes it to the next logger in the chain.
     *
     * @param level the log level of the message
     * @param message the message to log
     * @param loggerTarget the target for logging
     * @throws Exception if an error occurs during logging
     */
    public void logMessage(String level, String message, LoggerTarget loggerTarget) throws Exception {
        if (this.level.equals(level)) {
            display(level, message, loggerTarget);
        }
        if (nextLevelLogger != null) {
            nextLevelLogger.logMessage(level, message, loggerTarget);
        }
    }

    /**
     * Displays the log message to the specified target.
     *
     * @param level the log level of the message
     * @param message the message to log
     * @param loggerTarget the target for logging
     * @throws Exception if an error occurs during logging
     */
    protected abstract void display(String level, String message, LoggerTarget loggerTarget) throws Exception;
}