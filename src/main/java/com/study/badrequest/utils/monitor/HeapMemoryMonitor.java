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
     * init = 초기 상태 상태의 메모리
     * used = 사용중인 메모
     * committed = 할당된 메모리
     * max = 총 메모리
     */
    public HeapMemoryDto monitor() {

        /**
         * 자바 애플리케이션에서 힙 메모리 사용에 관련된 여러 지표를 검색하려고하는 것 같습니다.
         *
         * ManagementFactory.getMemoryMXBean() 메서드는 MemoryMXBean의 인스턴스를 반환하여 자바 가상 머신의 메모리 사용에 대한 정보를 제공합니다.
         *
         * getHeapMemoryUsage() 메서드에서 반환 된 heapMemoryUsage 객체는 현재 힙 메모리의 사용에 대한 정보를 포함하여 힙
         * 메모리의 초기 크기 (heapMemoryInit), 현재 사용 중인 힙 메모리의 양 (heapMemoryUsed),
         * 자바 가상 머신에서 사용할 힙 메모리의 양 (heapMemoryUsageCommitted),
         * 그리고 메모리 관리에 사용할 수 있는 힙 메모리의 최대 양 (heapMemoryUsageMax)을 포함합니다.
         */

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
