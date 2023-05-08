package com.study.badrequest.utils.monitor;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;


@Component
@RequiredArgsConstructor
@Slf4j
public class HeapMemoryMonitor {

    /**
     * init = 초기 상태의 메모리
     * used = 현재 사용중인 메모
     * committed =현재 할당된 메모리(heap 에 JVM 이 할당한)
     * max = 사용할 수 있는 최대 메모리(heap 에 JVM 이 할당할 수 있는 최대)
     */
    public HeapMemoryDto monitor() {

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long heapMemoryInit = heapMemoryUsage.getInit();
        long heapMemoryUsed = heapMemoryUsage.getUsed();
        long heapMemoryUsageCommitted = heapMemoryUsage.getCommitted();
        long heapMemoryUsageMax = heapMemoryUsage.getMax();

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        long nonHeapMemoryInit = nonHeapMemoryUsage.getInit();
        long nonHeapMemoryUsed = nonHeapMemoryUsage.getUsed();
        long nonHeapMemoryCommitted = nonHeapMemoryUsage.getCommitted();
        long nonHeapMemoryMax = nonHeapMemoryUsage.getMax();

        return HeapMemoryDto.builder()
                .heapMemoryInit(heapMemoryInit)
                .heapMemoryUsed(heapMemoryUsed)
                .heapMemoryUsageCommitted(heapMemoryUsageCommitted)
                .heapMemoryUsageMax(heapMemoryUsageMax)
                .nonHeapMemoryInit(nonHeapMemoryInit)
                .nonHeapMemoryUsed(nonHeapMemoryUsed)
                .nonHeapMemoryCommitted(nonHeapMemoryCommitted)
                .nonHeapMemoryMax(nonHeapMemoryMax)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HeapMemoryDto {
        private long heapMemoryInit;
        private long heapMemoryUsed;
        private long heapMemoryUsageCommitted;
        private long heapMemoryUsageMax;

        private long nonHeapMemoryInit;
        private long nonHeapMemoryUsed;
        private long nonHeapMemoryCommitted;
        private long nonHeapMemoryMax;
    }
}
