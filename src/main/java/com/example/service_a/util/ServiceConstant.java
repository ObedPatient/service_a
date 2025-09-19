/**
 * Provides constant values for log levels, archive strategies,
 * and metadata types used across the service.
 *
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.util;

import java.util.Arrays;
import java.util.List;

public class ServiceConstant {

    /**
     * List of valid log levels that can be used for logging.
     */
    public static final List<String> VALID_LOG_LEVELS = Arrays.asList(
            "INFO",
            "ERROR",
            "WARNING",
            "DEBUG"
    );

    /**
     * List of valid archive strategies for handling logs.
     */
    public static final List<String> VALID_ARCHIVE_STRATEGIES = Arrays.asList(
            "DELETE",
            "ARCHIVE",
            "NEVER"
    );

    /**
     * List of valid metadata types associated with logs.
     */
    public static final List<String> VALID_METADATA_TYPES = Arrays.asList(
            "QUERY",
            "STACKTRACE",
            "OBJECT",
            "MESSAGE",
            "ERROR MESSAGE"
    );
}
