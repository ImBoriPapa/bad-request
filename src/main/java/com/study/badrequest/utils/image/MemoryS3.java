package com.study.badrequest.utils.image;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
public class MemoryS3 {
    private Long id = new AtomicLong().incrementAndGet();
    private Map<Long, MockS3Image> mockData = new HashMap<>();

    public void putObjectRequest(String bucket, String storedName, InputStream inputStream, ObjectMetadata objectMetadata) {

        MockS3Image mockS3Image = MockS3Image.builder()
                .bucket(bucket)
                .storedName(storedName)
                .inputStream(inputStream)
                .objectMetadata(objectMetadata)
                .build();

        mockData.put(id, mockS3Image);
    }
}
