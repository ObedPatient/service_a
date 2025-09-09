package com.example.service_a.util.logging.manager;

import com.example.service_a.util.logging.chain.*;
import com.example.service_a.producer.KafkaLogger;
import com.example.service_a.util.logging.target.LoggerTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LogManager {
    private final LoggerTarget loggerTarget;
    private static KafkaLogger kafkaLogger;

    @Autowired
    public LogManager(LoggerTarget loggerTarget) {
        this.loggerTarget = loggerTarget;
    }

    @Autowired
    public void setKafkaLogger(@Lazy KafkaLogger kafkaLogger) {
        LogManager.kafkaLogger = kafkaLogger;
    }

    public AbstractLogger doChaining() {
        AbstractLogger errorLogger = new ErrorLogger("ERROR");
        AbstractLogger warningLogger = new WarningLogger("WARNING");
        AbstractLogger infoLogger = new InfoLogger("INFO");
        AbstractLogger debugLogger = new DebugLogger("DEBUG");

        errorLogger.setNextLevelLogger(warningLogger);
        warningLogger.setNextLevelLogger(infoLogger);
        infoLogger.setNextLevelLogger(debugLogger);

        return errorLogger;
    }

    public LoggerTarget addObservers() {
        loggerTarget.addObserver("INFO", kafkaLogger);
        loggerTarget.addObserver("ERROR", kafkaLogger);
        loggerTarget.addObserver("WARNING", kafkaLogger);
        loggerTarget.addObserver("DEBUG", kafkaLogger);
        return loggerTarget;
    }
}