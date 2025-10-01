/**
 * Aspect for logging audit information for UserService method executions and exceptions.
 * @author  - Obed Patient
 * @version - 1.0
 * @since   - 1.0
 */
package com.example.service_a.component.aop;

import com.example.service_a.dto.AuditLogDto;
import com.example.service_a.dto.ActionDto;
import com.example.service_a.dto.MetadataDto;
import com.example.service_a.dto.UserFlatDto;
import com.example.service_a.component.AuditLogUtil;
import com.example.service_a.util.UserIdGenerator;
import com.example.service_a.component.Logger;
import com.example.service_a.model.UserModel;
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

        UserFlatDto performer;
        if (result instanceof UserModel userModel) {
            performer = UserFlatDto.builder()
                    .performerId(userModel.getPerformerId())
                    .firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName())
                    .workEmail(userModel.getWorkEmail())
                    .phoneNumber(userModel.getPhoneNumber())
                    .build();
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

    /**
     * Logs audit information when a UserService method throws an exception.
     *
     * @param joinPoint the join point of the method execution
     * @param ex the exception thrown by the method
     * @throws Exception if an error occurs during logging
     */
    @AfterReturning(pointcut = "execution(* com.example.service_a.service..*.*(..))", returning = "result")
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

        UserFlatDto performer = UserFlatDto.builder()
                .performerId(UserIdGenerator.generateId())
                .firstName("David")
                .lastName("Semana")
                .workEmail("semana@gmail.com")
                .phoneNumber("0789278490")
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

    /**
     * Retrieves the client IP address from the HTTP request.
     *
     * @return the client IP address
     * @throws UnknownHostException if the IP address cannot be resolved
     */
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

    /**
     * Generates content for audit log based on result and metadata type.
     *
     * @param result the result of the method execution
     * @param metadataType the type of metadata
     * @return the generated content as a string
     * @throws Exception if an error occurs during content generation
     */
    private String generateContent(Object result, String metadataType) throws Exception {
        if ("OBJECT".equals(metadataType)) {
            return objectMapper.writeValueAsString(result);
        } else {
            return result != null ? result.toString() : "No result";
        }
    }

    /**
     * Generates content for audit log based on exception and metadata type.
     *
     * @param ex the exception thrown
     * @param metadataType the type of metadata
     * @return the generated content as a string
     */
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