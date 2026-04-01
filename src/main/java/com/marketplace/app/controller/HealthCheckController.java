package com.marketplace.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

/**
 * HealthCheckController
 * 
 * Provides comprehensive health-check and monitoring endpoints for the Mini Marketplace application.
 * Includes system status, resource usage, and dependency health checks.
 * Useful for application monitoring and health verification in production environments.
 * 
 * Base route: /api/health
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    /**
     * Basic health check endpoint
     * Returns simple UP status with current timestamp
     * 
     * @return response with status, timestamp, and service name
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> basicHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Mini Marketplace");
        return ResponseEntity.ok(response);
    }

    /**
     * Ping endpoint for simple connectivity check
     * 
     * @return "pong" response string
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    /**
     * Detailed status endpoint with JVM information
     * Includes uptime and Java version
     * 
     * @return response with detailed status information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> detailedStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("time", LocalDateTime.now());
        response.put("uptime", getUptime());
        response.put("javaVersion", System.getProperty("java.version"));
        return ResponseEntity.ok(response);
    }

    /**
     * System information endpoint
     * Returns operating system and processor details
     * 
     * @return response with OS, version, architecture, and processor count
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> systemInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("os", System.getProperty("os.name"));
        data.put("osVersion", System.getProperty("os.version"));
        data.put("arch", System.getProperty("os.arch"));
        data.put("processors", Runtime.getRuntime().availableProcessors());
        return ResponseEntity.ok(data);
    }

    /**
     * Memory usage endpoint
     * Returns JVM heap and non-heap memory information
     * 
     * @return response with heap used, heap max, and non-heap used values
     */
    @GetMapping("/memory")
    public ResponseEntity<Map<String, Object>> memoryInfo() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Map<String, Object> memory = new HashMap<>();
        memory.put("heapUsed", memoryMXBean.getHeapMemoryUsage().getUsed());
        memory.put("heapMax", memoryMXBean.getHeapMemoryUsage().getMax());
        memory.put("nonHeapUsed", memoryMXBean.getNonHeapMemoryUsage().getUsed());
        return ResponseEntity.ok(memory);
    }

    /**
     * CPU information endpoint
     * Returns CPU load and available processor information
     * 
     * @return response with system load average and processor count
     */
    @GetMapping("/cpu")
    public ResponseEntity<Map<String, Object>> cpuInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Map<String, Object> cpu = new HashMap<>();
        cpu.put("loadAverage", osBean.getSystemLoadAverage());
        cpu.put("availableProcessors", osBean.getAvailableProcessors());
        return ResponseEntity.ok(cpu);
    }

    /**
     * Database connectivity check
     * Returns mock database status
     * 
     * @return response with database connection status
     */
    @GetMapping("/db-check")
    public ResponseEntity<Map<String, String>> databaseCheck() {
        Map<String, String> result = new HashMap<>();
        result.put("database", "connected");
        result.put("status", "OK");
        return ResponseEntity.ok(result);
    }

    /**
     * Cache availability check
     * Returns mock cache status
     * 
     * @return response with cache availability status
     */
    @GetMapping("/cache-check")
    public ResponseEntity<Map<String, String>> cacheCheck() {
        Map<String, String> result = new HashMap<>();
        result.put("cache", "available");
        result.put("status", "OK");
        return ResponseEntity.ok(result);
    }

    /**
     * External API reachability check
     * Returns mock external API status
     * 
     * @return response with external service status
     */
    @GetMapping("/external-api")
    public ResponseEntity<Map<String, String>> externalApiCheck() {
        Map<String, String> result = new HashMap<>();
        result.put("externalService", "reachable");
        result.put("status", "OK");
        return ResponseEntity.ok(result);
    }

    /**
     * Complete health summary endpoint
     * Combines information from all health checks
     * 
     * @return comprehensive response with all health metrics
     */
    @GetMapping("/full")
    public ResponseEntity<Map<String, Object>> fullHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("basic", basicHealth().getBody());
        health.put("system", systemInfo().getBody());
        health.put("memory", memoryInfo().getBody());
        health.put("cpu", cpuInfo().getBody());
        health.put("db", databaseCheck().getBody());
        health.put("cache", cacheCheck().getBody());
        health.put("timestamp", LocalDateTime.now());
        health.put("overallStatus", "UP");
        return ResponseEntity.ok(health);
    }

    /**
     * Application version endpoint
     * Returns application name and version information
     * 
     * @return response with version details
     */
    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> version() {
        return ResponseEntity.ok(Map.of(
                "app", "Mini Marketplace",
                "version", "1.0.0",
                "build", "stable"
        ));
    }

    /**
     * Environment information endpoint
     * Returns Java home and user environment variables
     * 
     * @return response with environment details
     */
    @GetMapping("/env")
    public ResponseEntity<Map<String, String>> environment() {
        Map<String, String> env = new HashMap<>();
        env.put("java_home", System.getenv("JAVA_HOME"));
        env.put("user", System.getenv("USER"));
        return ResponseEntity.ok(env);
    }

    /**
     * Thread information endpoint
     * Returns active thread count and current thread name
     * 
     * @return response with thread information
     */
    @GetMapping("/thread")
    public ResponseEntity<Map<String, Object>> threadInfo() {
        Map<String, Object> thread = new HashMap<>();
        thread.put("threadCount", Thread.activeCount());
        thread.put("currentThread", Thread.currentThread().getName());
        return ResponseEntity.ok(thread);
    }

    /**
     * Current time and timezone endpoint
     * Returns server current time and timezone information
     * 
     * @return response with time and timezone
     */
    @GetMapping("/time")
    public ResponseEntity<Map<String, Object>> time() {
        return ResponseEntity.ok(Map.of(
                "now", LocalDateTime.now(),
                "zone", TimeZone.getDefault().getID()
        ));
    }

    /**
     * Health summary endpoint
     * Provides quick list of performed health checks
     * 
     * @return response with overall health status and check list
     */
    @GetMapping("/health-summary")
    public ResponseEntity<Map<String, Object>> summary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("status", "UP");
        summary.put("checks", List.of("db", "cache", "api"));
        summary.put("time", LocalDateTime.now());
        return ResponseEntity.ok(summary);
    }

    /**
     * Helper method to get JVM uptime
     * 
     * @return uptime in milliseconds
     */
    private long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    /**
     * Helper method to build standard response map
     * 
     * @param key   the response key
     * @param value the response value
     * @return map with key, value, and timestamp
     */
    private Map<String, Object> buildResponse(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        map.put("time", LocalDateTime.now());
        return map;
    }
}