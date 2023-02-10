package com.study.badrequest.domain.admin;


import com.fasterxml.jackson.databind.util.ObjectBuffer;
import com.study.badrequest.utils.monitor.HeapMemoryMonitor;
import com.study.badrequest.utils.monitor.SystemMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitorService {

    private final SystemMonitor systemMonitor;
    private final HeapMemoryMonitor heapMemoryMonitor;

    private final long SSE_EMITTER_TIME_OUT = 60 * 1000L;

    private final int THREAD_DELAY = 5000;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private void sendData(SseEmitter sseEmitter, Object data) {
        try {
            this.emitters.add(sseEmitter);
            sseEmitter.send(SseEmitter.event()
                    .name("data")
                    .data(data));

            String dataName = data.getClass().getSimpleName();

            sseEmitter.complete();
            sseEmitter.onCompletion(() -> log.debug("[SEND " + dataName + " Data Completion]"
                    , this.emitters.remove(sseEmitter)));

            Thread.sleep(THREAD_DELAY);
        } catch (Exception e) {
            log.error("Error", e);
            sseEmitter.completeWithError(e);
        }

    }

    public SseEmitter suppleSystemData() {
        SseEmitter sseEmitter = new SseEmitter(SSE_EMITTER_TIME_OUT);

        CompletableFuture.runAsync(() -> {
            sendData(sseEmitter,
                    systemMonitor.monitor());
        });

        return sseEmitter;
    }

    public SseEmitter suppleHeapData() {
        SseEmitter sseEmitter = new SseEmitter(SSE_EMITTER_TIME_OUT);

        CompletableFuture.runAsync(() -> {
            sendData(sseEmitter, heapMemoryMonitor.monitor());
        });

        return sseEmitter;

    }
}
