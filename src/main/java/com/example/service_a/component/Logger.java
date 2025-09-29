package com.example.service_a.component;

import com.example.service_a.util.logging.chain.AbstractLogger;
import org.springframework.stereotype.Component;

@Component
public class Logger {
    private final AbstractLogger chain;
    private final LoggerTarget loggerTarget;

    public Logger(LogManager logManager) {
        this.chain = logManager.doChaining();
        this.loggerTarget = logManager.addObservers();
    }

    public void log(String level, String message) throws Exception {
        chain.logMessage(level, message, loggerTarget);
    }

    public void error(String message) throws Exception {
        chain.logMessage("ERROR", message, loggerTarget);
    }
}