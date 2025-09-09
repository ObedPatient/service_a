package com.example.service_a.util.logging.chain;

// Comment: Handles ERROR level logs

import com.example.service_a.util.logging.target.LoggerTarget;

public class ErrorLogger extends AbstractLogger {
    public ErrorLogger(String level) {
        this.level = level;
    }

    @Override
    protected void display(String level, String message, LoggerTarget loggerTarget) throws Exception {
        loggerTarget.notifyObservers(level, message);
    }
}