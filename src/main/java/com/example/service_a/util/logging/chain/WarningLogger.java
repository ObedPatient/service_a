package com.example.service_a.util.logging.chain;

import com.example.service_a.util.logging.target.LoggerTarget;

// Handles WARNING level logs
public class WarningLogger extends AbstractLogger {
    public WarningLogger(String level) {
        this.level = level;
    }

    @Override
    protected void display(String level, String message, LoggerTarget loggerTarget) throws Exception {
        loggerTarget.notifyObservers(level, message);
    }
}