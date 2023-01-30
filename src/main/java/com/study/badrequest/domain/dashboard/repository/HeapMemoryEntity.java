package com.study.badrequest.domain.dashboard.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
public class HeapMemoryEntity {

    private LocalDateTime date;
    private long heapMemoryInit;
    private long heapMemoryUsed;
    private long heapMemoryUsageCommitted;
    private long heapMemoryUsageMax;
    private long nonHeapMemoryInit;
    private long nonHeapMemoryUsed;
    private long nonHeapMemoryCommitted;
    private long nonHeapMemoryMax;

    @Builder
    public HeapMemoryEntity(LocalDateTime date, long heapMemoryInit, long heapMemoryUsed, long heapMemoryUsageCommitted, long heapMemoryUsageMax, long nonHeapMemoryInit, long nonHeapMemoryUsed, long nonHeapMemoryCommitted, long nonHeapMemoryMax) {
        this.date = date;
        this.heapMemoryInit = heapMemoryInit;
        this.heapMemoryUsed = heapMemoryUsed;
        this.heapMemoryUsageCommitted = heapMemoryUsageCommitted;
        this.heapMemoryUsageMax = heapMemoryUsageMax;
        this.nonHeapMemoryInit = nonHeapMemoryInit;
        this.nonHeapMemoryUsed = nonHeapMemoryUsed;
        this.nonHeapMemoryCommitted = nonHeapMemoryCommitted;
        this.nonHeapMemoryMax = nonHeapMemoryMax;
    }

}
