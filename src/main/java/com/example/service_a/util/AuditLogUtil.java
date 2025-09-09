package com.example.service_a.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditLogUtil {

    public List<String> getValidLogLevels() {
        List<String> logLevelNames = ServiceConstant.VALID_LOG_LEVELS;
        if (logLevelNames.isEmpty()) {
            throw new IllegalStateException("No valid log levels available");
        }
        return logLevelNames;
    }

    public List<String> getValidArchiveStrategies() {
        List<String> archiveStrategyNames = ServiceConstant.VALID_ARCHIVE_STRATEGIES;
        if (archiveStrategyNames.isEmpty()) {
            throw new IllegalStateException("No valid archive strategies available");
        }
        return archiveStrategyNames;
    }

    public List<String> getValidMetadataTypes() {
        List<String> metadataTypeNames = ServiceConstant.VALID_METADATA_TYPES;
        if (metadataTypeNames.isEmpty()) {
            throw new IllegalStateException("No valid metadata types available");
        }
        return metadataTypeNames;
    }

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

    public String determineArchiveStrategy(String methodName) {
        if (methodName.contains("delete") || methodName.contains("remove")) {
            return "DELETE";
        } else if (methodName.contains("debug") || methodName.contains("trace")) {
            return "NEVER";
        } else {
            return "ARCHIVE";
        }
    }

    public String determineMetadataType(Object result, String methodName) {
        if (methodName.contains("object") || (result != null && !(result instanceof String))) {
            return "OBJECT";
        } else if (methodName.contains("message") || methodName.contains("debug") || methodName.contains("trace")) {
            return "MESSAGE";
        } else {
            return "MESSAGE";
        }
    }

    public String determineMetadataType(Throwable ex, String methodName) {
        if (methodName.contains("error") || methodName.contains("fail")) {
            return "ERROR MESSAGE";
        } else {
            return "STACKTRACE";
        }
    }
}