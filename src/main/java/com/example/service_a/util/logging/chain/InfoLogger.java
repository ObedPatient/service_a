package com.example.service_a.util.logging.chain;

import com.example.service_a.util.logging.target.LoggerTarget;

public class InfoLogger extends AbstractLogger{
    public InfoLogger(String level) {
        this.level = level;
    }

    @Override
    protected void display(String level, String message, LoggerTarget loggerTarget) throws Exception {
        loggerTarget.notifyObservers(level, message);
    }
}
