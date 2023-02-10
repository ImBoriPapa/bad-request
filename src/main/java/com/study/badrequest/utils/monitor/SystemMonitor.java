package com.study.badrequest.utils.monitor;

import com.sun.management.OperatingSystemMXBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

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

        cpuUsagePercent = getMemoryUsageSpace();
        memoryTotalSpace = getTotalMemorySize();
        memoryUsageSpace = getMemoryUsageSpace();
        memoryFreeSpace = getFreeMemorySize();


        return SystemMonitorDto.builder()
                .cpuUsagePercent(getCpuUsagePercent())
                .memoryFreeSpace(getFreeMemorySize())
                .memoryTotalSpace(getTotalMemorySize())
                .memoryUsageSpace(getMemoryUsageSpace())
                .build();
    }

    private double getCpuUsagePercent() {
        return Math.round(osBean.getSystemCpuLoad() * 100);
    }

    private double getTotalMemorySize() {
        return Math.round(byteToGigabyte(osBean.getTotalPhysicalMemorySize()) * 1000) / 1000.0;
    }

    private double getMemoryUsageSpace() {
        return getTotalMemorySize() - getFreeMemorySize();
    }

    private double getFreeMemorySize() {
        return Math.round(byteToGigabyte(osBean.getFreePhysicalMemorySize()) * 1000) / 1000.0;
    }

    private double byteToGigabyte(long b) {
        return (double) b / (1024 * 1024 * 1024);
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






