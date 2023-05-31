package com.study.badrequest.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Profile({"test", "dev"})
@Configuration
public class LocalStackConfig {

    //docker 이미지 명
    private final DockerImageName LOCALSTACK_NAME = DockerImageName.parse("localstack/localstack");
    @Value("${s3-image.bucket-name}")
    public String BUCKET_NAME;
    @Bean(initMethod = "start", destroyMethod = "stop")
    public LocalStackContainer localStackContainer() {
        return new LocalStackContainer(LOCALSTACK_NAME).withServices(S3);
    }

    @Bean
    public AmazonS3Client amazonS3Client(LocalStackContainer localStackContainer) {
        System.out.println("================Local S3 Start======================");

        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
                .withCredentials(localStackContainer.getDefaultCredentialsProvider())
                .build();

        amazonS3.createBucket(BUCKET_NAME);
        return (AmazonS3Client) amazonS3;
    }
}
