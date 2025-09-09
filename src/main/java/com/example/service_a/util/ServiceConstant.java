package com.example.service_a.util;

import java.util.Arrays;
import java.util.List;

public class ServiceConstant {
    public static final List<String> VALID_LOG_LEVELS = (Arrays.asList(
            "INFO",
            "ERROR",
            "WARNING",
            "DEBUG"
    ));

    public static final List<String> VALID_ARCHIVE_STRATEGIES = (Arrays.asList(
            "DELETE",
            "ARCHIVE",
            "NEVER"
    ));

    public static final List<String> VALID_METADATA_TYPES = (Arrays.asList(
            "QUERY",
            "STACKTRACE",
            "OBJECT",
            "MESSAGE",
            "ERROR MESSAGE"
    ));
}
