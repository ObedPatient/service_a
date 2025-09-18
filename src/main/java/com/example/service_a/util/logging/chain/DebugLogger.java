package com.example.service_a.util.logging.chain;

import com.example.service_a.util.logging.target.LoggerTarget;

// Handles DEBUG level logs
public class DebugLogger extends AbstractLogger {
    public DebugLogger(String level) {
        this.level = level;
    }

    @Override
    protected void display(String level, String message, LoggerTarget loggerTarget) throws Exception {
        loggerTarget.notifyObservers(level, message);
    }
}