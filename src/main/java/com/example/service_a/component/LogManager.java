/**
 * Manages logging operations by setting up a chain of loggers and adding observers for different log levels.
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.component;

import com.example.service_a.util.logging.chain.*;
import com.example.service_a.producer.KafkaLogger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LogManager {
    private final LoggerTarget loggerTarget;
    private final KafkaLogger kafkaLogger;

    /**
     * Constructs a LogManager with the specified LoggerTarget and KafkaLogger.
     * @param loggerTarget the target for logging operations
     * @param kafkaLogger the KafkaLogger for logging
     */
    public LogManager(LoggerTarget loggerTarget, @Lazy KafkaLogger kafkaLogger) {
        this.loggerTarget = loggerTarget;
        this.kafkaLogger = kafkaLogger;
    }

    /**
     * Creates a chain of loggers for different log levels.
     * @return the head of the logger chain
     */
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

    /**
     * Adds observers for different log levels to the LoggerTarget.
     * @return the LoggerTarget with added observers
     */
    public LoggerTarget addObservers() {
        if (kafkaLogger != null) {
            loggerTarget.addObserver("INFO", kafkaLogger);
            loggerTarget.addObserver("ERROR", kafkaLogger);
            loggerTarget.addObserver("WARNING", kafkaLogger);
            loggerTarget.addObserver("DEBUG", kafkaLogger);
        } else {
            System.err.println("KafkaLogger is null, no observers added");
        }
        return loggerTarget;
    }
}