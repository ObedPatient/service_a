/**
 * Aspect for logging audit information for UserService method executions and exceptions.
 * @author Obed Patient
 * @version 1.3
 * @since 1.0
 */
package com.example.service_a.component.aop;

import com.example.service_a.dto.AuditLogDto;
import com.example.service_a.dto.ActionDto;
import com.example.service_a.dto.MetadataDto;
import com.example.service_a.dto.UserFlatDto;
import com.example.service_a.component.AuditLogUtil;
import com.example.service_a.component.Logger;
import com.example.service_a.model.UserModel;
import com.example.service_a.util.ServiceConstant;
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
    public String serviceName;

    /**
     * Logs audit information after a UserService method executes successfully.
     *
     * @param joinPoint the join point of the method execution
     * @param result the result returned by the method
     * @throws Exception if an error occurs during logging
     */
    @AfterReturning(pointcut = "execution(* com.example.service_a.service..*.*(..))", returning = "result")
    public void logMethodExecution(JoinPoint joinPoint, Object result) throws Exception {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String ipAddress = getClientIp();
        String tokenId = "unknown";
        String logLevel = auditLogUtil.determineLogLevel(methodName);
        String archiveStrategy = auditLogUtil.determineArchiveStrategy(methodName);
        String timeToArchive = auditLogUtil.getTimeToArchive(archiveStrategy, logLevel);
        String description = (result instanceof UserModel ? "User operation" : "Operation") + " in " + methodName + " in " + className;
        String metadataType = auditLogUtil.determineMetadataType(result, methodName);
        String content = generateContent(result, metadataType);

        // Determine if this is a user creation event
        boolean isUserCreation = result instanceof UserModel;

        UserFlatDto performer = null;
        String performerId = "unknown";
        if (result instanceof UserModel userModel) {
            performer = UserFlatDto.builder()
                    .performerId(userModel.getPerformerId())
                    .firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName())
                    .workEmail(userModel.getWorkEmail())
                    .phoneNumber(userModel.getPhoneNumber())
                    .build();
            performerId = userModel.getPerformerId();
            content = objectMapper.writeValueAsString(performer);
        } else {
            performer = UserFlatDto.builder()
                    .performerId("unknown")
                    .firstName("Unknown")
                    .lastName("Unknown")
                    .workEmail("unknown@gmail.com")
                    .phoneNumber("unknown")
                    .build();
        }

        AuditLogDto auditLogDto = AuditLogDto.builder()
                .ipAddress(ipAddress)
                .serviceName(serviceName)
                .serverUser(ServiceConstant.SERVER_USER)
                .tokenId(tokenId)
                .logLevel(logLevel)
                .archiveStrategy(archiveStrategy)
                .timeToArchiveInDays(timeToArchive)
                .performerId(performerId)
                .metadata(MetadataDto.builder()
                                .content(content)
                                .metadataType(metadataType)
                                .isUserCreation(isUserCreation)
                                .build()
                )
                .action(ActionDto.builder()
                                .name(methodName)
                                .description(description)
                                .build()
                )
                .build();

        // Log audit information
        String auditMessage = objectMapper.writeValueAsString(auditLogDto);
        logger.log(logLevel, auditMessage);
    }

    /**
     * Logs audit information when a UserService method throws an exception.
     *
     * @param joinPoint the join point of the method execution
     * @param ex the exception thrown by the method
     * @throws Exception if an error occurs during logging
     */
    @AfterThrowing(pointcut = "execution(* com.example.service_a.service..*.*(..))", throwing = "ex")
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

        AuditLogDto auditLogDto = AuditLogDto.builder()
                .ipAddress(ipAddress)
                .serviceName(serviceName)
                .serverUser(ServiceConstant.SERVER_USER)
                .tokenId(tokenId)
                .logLevel(logLevel)
                .archiveStrategy(archiveStrategy)
                .timeToArchiveInDays(timeToArchive)
                .performerId("unknown")
                .metadata(MetadataDto.builder()
                                .content(content)
                                .metadataType(metadataType)
                                .isUserCreation(false)
                                .build()
                )
                .action(ActionDto.builder()
                                .name(methodName)
                                .description(description)
                                .build()
                )
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
        if ("OBJECT".equals(metadataType) && !(result instanceof UserModel)) {
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