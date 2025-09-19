/**
 * Logger for handling WARNING level logs in the Chain of Responsibility pattern.
 *
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.util.logging.chain;

import com.example.service_a.component.LoggerTarget;


public class WarningLogger extends AbstractLogger {
    /**
     * Constructs a WarningLogger with the specified log level.
     *
     * @param level the log level for this logger
     */
    public WarningLogger(String level) {
        this.level = level;
    }

    /**
     * Displays the WARNING level log message to the specified target.
     *
     * @param level the log level of the message
     * @param message the message to log
     * @param loggerTarget the target for logging
     * @throws Exception if an error occurs during logging
     */
    @Override
    protected void display(String level, String message, LoggerTarget loggerTarget) throws Exception {
        loggerTarget.notifyObservers(level, message);
    }
}