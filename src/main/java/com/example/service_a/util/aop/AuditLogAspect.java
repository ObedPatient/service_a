package com.example.service_a.util.aop;

import com.example.service_a.dto.AuditLogDto;
import com.example.service_a.dto.ActionDto;
import com.example.service_a.dto.MetadataDto;
import com.example.service_a.dto.UserFlatDto;
import com.example.service_a.util.AuditLogUtil;
import com.example.service_a.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.io.PrintWriter;
import java.io.StringWriter;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final Logger logger;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final AuditLogUtil auditLogUtil;

    @Value("${app.service-name}")
    private String serviceName;

    @AfterReturning(pointcut = "execution(* com.example.service_a.service..*.*(..))",
            returning = "result")
    public void logMethodExecution(JoinPoint joinPoint, Object result) throws Exception {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String ipAddress = getClientIp();
        String tokenId = "unknown";
        String logLevel = auditLogUtil.determineLogLevel(methodName);
        String archiveStrategy = auditLogUtil.determineArchiveStrategy(methodName);
        String timeToArchive = auditLogUtil.getTimeToArchive(archiveStrategy, logLevel);
        String description = "Execution of " + methodName + " in " + className;
        String metadataType = auditLogUtil.determineMetadataType(result, methodName);
        String content = generateContent(result, metadataType);

        // Placeholder for user details; in a real system, fetch from authentication context
        UserFlatDto performer = UserFlatDto.builder()
                .performerId("unknown") // Replace with actual logic, e.g., from SecurityContext
                .firstName("Unknown") // Replace with actual logic
                .lastName("Unknown") // Replace with actual logic
                .workEmail("unknown@example.com") // Replace with actual logic
                .phoneNumber("unknown") // Replace with actual logic
                .build();

        AuditLogDto auditLogDto = AuditLogDto.builder()
                .ipAddress(ipAddress)
                .serviceName(serviceName)
                .tokenId(tokenId)
                .logLevel(logLevel)
                .archiveStrategy(archiveStrategy)
                .timeToArchiveInDays(timeToArchive)
                .performer(performer)
                .action(List.of(
                        ActionDto.builder()
                                .name(methodName)
                                .description(description)
                                .build()
                ))
                .metadata(List.of(
                        MetadataDto.builder()
                                .content(content)
                                .metadataType(metadataType)
                                .build()
                ))
                .build();

        String message = objectMapper.writeValueAsString(auditLogDto);
        logger.log(logLevel, message);
    }

    @AfterThrowing(pointcut = "execution(* com.example.service_a.service..*.*(..))",
            throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) throws Exception {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String ipAddress = getClientIp();
        String tokenId = "unknown";
        String logLevel = "ERROR";
        String archiveStrategy = "ARCHIVE";
        String timeToArchive = auditLogUtil.getTimeToArchive(archiveStrategy, logLevel);
        String description = "Exception in " + methodName + " in " + className;
        String metadataType = auditLogUtil.determineMetadataType(ex, methodName);
        String content = generateContent(ex, metadataType);

        // Placeholder for user details; in a real system, fetch from authentication context
        UserFlatDto performer = UserFlatDto.builder()
                .performerId("unknown")
                .firstName("Unknown")
                .lastName("Unknown")
                .workEmail("unknown@example.com")
                .phoneNumber("unknown")
                .build();

        AuditLogDto auditLogDto = AuditLogDto.builder()
                .ipAddress(ipAddress)
                .serviceName(serviceName)
                .tokenId(tokenId)
                .logLevel(logLevel)
                .archiveStrategy(archiveStrategy)
                .timeToArchiveInDays(timeToArchive)
                .performer(performer)
                .action(List.of(
                        ActionDto.builder()
                                .name(methodName)
                                .description(description)
                                .build()
                ))
                .metadata(List.of(
                        MetadataDto.builder()
                                .content(content)
                                .metadataType(metadataType)
                                .build()
                ))
                .build();

        String message = objectMapper.writeValueAsString(auditLogDto);
        logger.log(logLevel, message);
    }

    private String getClientIp() throws UnknownHostException {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        return ip;
    }

    private String generateContent(Object result, String metadataType) throws Exception {
        if ("OBJECT".equals(metadataType)) {
            return objectMapper.writeValueAsString(result);
        } else {
            return result != null ? result.toString() : "No result";
        }
    }

    private String generateContent(Throwable ex, String metadataType) {
        if ("STACKTRACE".equals(metadataType)) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return sw.toString();
        } else {
            return ex.getMessage() != null ? ex.getMessage() : "No error message";
        }
    }
}