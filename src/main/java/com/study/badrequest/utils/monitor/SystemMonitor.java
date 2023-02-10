package com.study.badrequest.utils.monitor;

import com.sun.management.OperatingSystemMXBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

import static com.study.badrequest.utils.monitor.SystemMonitor.SystemDataType.*;

@Component
public class SystemMonitor {
    // CPU 사용량 %
    private double cpuUsagePercent;
    // 메모리 총 공간 GB
    private double memoryTotalSpace;
    // 사용중인 메모리  공간 GB
    private double memoryUsageSpace;
    // 사용 가능한 메모리 공간 GB
    private double memoryFreeSpace;
    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public SystemMonitorDto monitor() {

        cpuUsagePercent = getSystemData(CPU, osBean);
        memoryTotalSpace = getSystemData(TOTAL, osBean);
        memoryUsageSpace = getSystemData(USAGE, osBean);
        memoryFreeSpace = getSystemData(FREE, osBean);


        return SystemMonitorDto.builder()
                .cpuUsagePercent(cpuUsagePercent)
                .memoryTotalSpace(memoryTotalSpace)
                .memoryUsageSpace(memoryUsageSpace)
                .memoryFreeSpace(memoryFreeSpace)
                .build();
    }

    private double getSystemData(SystemDataType type, OperatingSystemMXBean osBean) {

        switch (type) {
            case CPU:
                return Math.round(osBean.getSystemCpuLoad() * 100);
            case TOTAL:
                return getTotalMemorySize();

            case USAGE:
                return getMemoryUsageSpace();

            case FREE:
                return getFreeMemorySize();

            default:
                return 0.0;
        }

    }

    /**
     * 메모리 총 사이즈 - 메모리 여유 사이즈
     */
    private double getMemoryUsageSpace() {
        return getTotalMemorySize() - getFreeMemorySize();
    }
    private double getTotalMemorySize() {
        return Math.round(byteToGigabyte(osBean.getTotalPhysicalMemorySize()) * 1000) / 1000.0;
    }

    private double getFreeMemorySize() {
        return Math.round(byteToGigabyte(osBean.getFreePhysicalMemorySize()) * 1000) / 1000.0;
    }

    /**
     * Byte -> GigaByte
     */
    private double byteToGigabyte(long b) {
        return (double) b / (1024 * 1024 * 1024);
    }


    enum SystemDataType {
        CPU,
        TOTAL,
        USAGE,
        FREE
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SystemMonitorDto {
        private double cpuUsagePercent;
        private double memoryTotalSpace;
        private double memoryUsageSpace;
        private double memoryFreeSpace;
    }
}






