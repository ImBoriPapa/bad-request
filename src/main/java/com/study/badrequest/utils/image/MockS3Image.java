package com.study.badrequest.utils.image;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Getter
@NoArgsConstructor
public class MockS3Image {

    private String bucket;
    private String storedName;
    private InputStream inputStream;
    private ObjectMetadata objectMetadata;
    @Builder
    public MockS3Image(String bucket, String storedName, InputStream inputStream, ObjectMetadata objectMetadata) {
        this.bucket = bucket;
        this.storedName = storedName;
        this.inputStream = inputStream;
        this.objectMetadata = objectMetadata;
    }
}
