package com.study.badrequest.utils.monitor;

import com.sun.management.OperatingSystemMXBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

@Component
public class CpuMonitor {
    private OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public CpuMonitorDto monitor() {

        double cpuUsage = getCpuUsage();

        double memoryFreeSpace = getFreePhysicalMemorySize();

        double memoryTotalSpace = getPhysicalMemorySize();

        return CpuMonitorDto.builder()
                .cpuUsage(cpuUsage)
                .memoryFreeSpace(memoryFreeSpace)
                .memoryTotalSpace(memoryTotalSpace)
                .build();
    }

    private double getCpuUsage() {
        return Math.round(osBean.getSystemCpuLoad() * 100) / 100.0;
    }

    private double getPhysicalMemorySize() {
        return ((double) osBean.getTotalPhysicalMemorySize() / 1024 / 1024 / 1024);
//        return Math.round((double) osBean.getTotalPhysicalMemorySize() / 1024 / 1024 / 1024) / 100.0;
    }

    private double getFreePhysicalMemorySize() {
        double num = (double) osBean.getFreePhysicalMemorySize() / 1024 / 1024 / 1024;
        return Math.round(num * 100) / 100.0;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CpuMonitorDto {
        private double cpuUsage;
        private double memoryFreeSpace;
        private double memoryTotalSpace;
    }
}
