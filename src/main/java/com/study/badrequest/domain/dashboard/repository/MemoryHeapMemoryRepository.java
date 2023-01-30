package com.study.badrequest.domain.dashboard.repository;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@Component
public class MemoryHeapMemoryRepository {

    private static AtomicLong sequence = new AtomicLong();
    private static Map<Long, HeapMemoryEntity> store = new ConcurrentHashMap<>();

    public HeapMemoryEntity save(HeapMemoryEntity HeapMemoryEntity) {
        long id = sequence.incrementAndGet();

        store.put(id, HeapMemoryEntity);

        return store.get(id);
    }

    public List<HeapMemoryEntity> saveAll(Iterable<HeapMemoryEntity> heapMemory) {

        heapMemory.forEach(hp -> {
            store.put(sequence.incrementAndGet(), hp);
        });
        return new ArrayList<>(store.values());
    }

    public List<HeapMemoryEntity> findAll(){
        return new ArrayList<>(store.values());
    }

    public void clear(){
        store.clear();
    }
}
