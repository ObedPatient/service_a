/**
 * Utility class for managing audit logging operations, including log levels, archive strategies, and metadata types.
 * Provides methods to retrieve valid log levels, archive strategies, metadata types, and determine logging attributes based on method names and results.
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.component;

import com.example.service_a.util.ServiceConstant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditLogUtil {

    /**
     * Retrieves the list of valid log levels.
     * @return list of valid log level names
     * @throws IllegalStateException if no valid log levels are available
     */
    public List<String> getValidLogLevels() {
        List<String> logLevelNames = ServiceConstant.VALID_LOG_LEVELS;
        if (logLevelNames.isEmpty()) {
            throw new IllegalStateException("No valid log levels available");
        }
        return logLevelNames;
    }

    /**
     * Retrieves the list of valid archive strategies.
     * @return list of valid archive strategy names
     * @throws IllegalStateException if no valid archive strategies are available
     */
    public List<String> getValidArchiveStrategies() {
        List<String> archiveStrategyNames = ServiceConstant.VALID_ARCHIVE_STRATEGIES;
        if (archiveStrategyNames.isEmpty()) {
            throw new IllegalStateException("No valid archive strategies available");
        }
        return archiveStrategyNames;
    }

    /**
     * Retrieves the list of valid metadata types.
     * @return list of valid metadata type names
     * @throws IllegalStateException if no valid metadata types are available
     */
    public List<String> getValidMetadataTypes() {
        List<String> metadataTypeNames = ServiceConstant.VALID_METADATA_TYPES;
        if (metadataTypeNames.isEmpty()) {
            throw new IllegalStateException("No valid metadata types available");
        }
        return metadataTypeNames;
    }

    /**
     * Determines the time to archive based on the archive strategy and log level.
     * @param archiveStrategy the strategy for archiving logs
     * @param logLevel the log level of the message
     * @return the number of days as a string before archiving, or "9999" for never
     */
    public String getTimeToArchive(String archiveStrategy, String logLevel) {
        if ("DELETE".equals(archiveStrategy)) {
            return "0";
        } else if ("ARCHIVE".equals(archiveStrategy)) {
            return switch (logLevel) {
                case "ERROR" -> "30";
                case "WARNING" -> "15";
                case "INFO", "DEBUG" -> "7";
                default -> "7";
            };
        } else {
            return "9999"; // NEVER
        }
    }

    /**
     * Determines the log level based on the method name.
     * @param methodName the name of the method
     * @return the determined log level
     */
    public String determineLogLevel(String methodName) {
        if (methodName.contains("error") || methodName.contains("fail")) {
            return "ERROR";
        } else if (methodName.contains("update") || methodName.contains("modify")) {
            return "WARNING";
        } else if (methodName.contains("debug") || methodName.contains("trace")) {
            return "DEBUG";
        } else {
            return "INFO";
        }
    }

    /**
     * Determines the archive strategy based on the method name.
     * @param methodName the name of the method
     * @return the determined archive strategy
     */
    public String determineArchiveStrategy(String methodName) {
        if (methodName.contains("delete") || methodName.contains("remove")) {
            return "DELETE";
        } else if (methodName.contains("debug") || methodName.contains("trace")) {
            return "NEVER";
        } else {
            return "ARCHIVE";
        }
    }

    /**
     * Determines the metadata type based on the result and method name.
     * @param result the result of the method execution
     * @param methodName the name of the method
     * @return the determined metadata type
     */
    public String determineMetadataType(Object result, String methodName) {
        if (methodName.contains("object") || (result != null && !(result instanceof String))) {
            return "OBJECT";
        } else if (methodName.contains("message") || methodName.contains("debug") || methodName.contains("trace")) {
            return "MESSAGE";
        } else {
            return "MESSAGE";
        }
    }

    /**
     * Determines the metadata type based on an exception and method name.
     * @param ex the exception thrown
     * @param methodName the name of the method
     * @return the determined metadata type
     */
    public String determineMetadataType(Throwable ex, String methodName) {
        if (methodName.contains("error") || methodName.contains("fail")) {
            return "ERROR MESSAGE";
        } else {
            return "STACKTRACE";
        }
    }
}