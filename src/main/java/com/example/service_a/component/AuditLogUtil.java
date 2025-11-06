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
     * @return the number of minutes as a string before archiving, or "9999" for never
     */
    public Integer getTimeToArchive(String archiveStrategy, String logLevel) {
        if ("DELETE".equals(archiveStrategy)) {
            return 0;
        }
        if ("NEVER".equals(archiveStrategy)) {
            return 9999;
        }
        if ("ARCHIVE".equals(archiveStrategy)) {
            return switch (logLevel) {
                case "ERROR" -> ServiceConstant.ARCHIVE_TIME_ERROR;
                case "WARNING" -> ServiceConstant.ARCHIVE_TIME_WARNING;
                case "INFO", "DEBUG" -> ServiceConstant.ARCHIVE_TIME_DEFAULT;
                default -> ServiceConstant.ARCHIVE_TIME_DEFAULT;
            };
        }
        return 9999; // fallback
    }

    /**
     * Determines the log level based on the method name.
     * @param methodName the name of the method
     * @return the determined log level
     */
    public String determineLogLevel(String methodName) {
        if (methodName == null) {
            return "INFO";
        }
        String lowerCaseMethodName = methodName.toLowerCase();
        if (lowerCaseMethodName.contains("error") || lowerCaseMethodName.contains("fail")) {
            return "ERROR";
        } else if (lowerCaseMethodName.contains("update") || lowerCaseMethodName.contains("modify")) {
            return "WARNING";
        } else if (lowerCaseMethodName.contains("get") || lowerCaseMethodName.contains("debug") || lowerCaseMethodName.contains("trace")) {
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
        if (methodName == null) {
            return "ARCHIVE";
        }
        String lowerCaseMethodName = methodName.toLowerCase();
        if (lowerCaseMethodName.contains("delete") || lowerCaseMethodName.contains("remove")) {
            return "DELETE";
        } else if (lowerCaseMethodName.contains("debug") || lowerCaseMethodName.contains("trace")) {
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
        if (methodName == null) {
            return "MESSAGE";
        }
        String lowerCaseMethodName = methodName.toLowerCase();
        if (lowerCaseMethodName.contains("object") || (result != null && !(result instanceof String))) {
            return "OBJECT";
        } else if (lowerCaseMethodName.contains("message") || lowerCaseMethodName.contains("debug") || lowerCaseMethodName.contains("trace")) {
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
        if (methodName == null) {
            return "STACKTRACE";
        }
        String lowerCaseMethodName = methodName.toLowerCase();
        if (lowerCaseMethodName.contains("error") || lowerCaseMethodName.contains("fail")) {
            return "ERROR MESSAGE";
        } else {
            return "STACKTRACE";
        }
    }
}