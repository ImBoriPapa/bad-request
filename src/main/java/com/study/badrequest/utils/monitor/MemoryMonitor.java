package com.study.badrequest.utils.monitor;


import com.study.badrequest.domain.dashboard.repository.HeapMemoryEntity;
import com.study.badrequest.domain.dashboard.repository.MemoryHeapMemoryRepository;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryMonitor {

    private final MemoryHeapMemoryRepository memoryHeapMemoryRepository;
    private List<HeapMemoryEntity> list = new ArrayList<>();

    /**
     * init = 초기 상태 상태의 메모리
     * used = 사용중인 메모
     * committed = 할당된 메모리
     * max = 총 메모리
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
                .date(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
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
        private LocalDateTime date;
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
