package com.study.badrequest.utils.image;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemoryImageStore {

    private final List<Map<String, Resource>> memory = new ArrayList<>();

    private static final String memoryPath = "http://mock-s3/";

    public void transFerToMemory(MultipartFile file, String storedName){
        String fullPath = memoryPath + storedName;

        Map<String, Resource> map = new HashMap<>();

        Resource resource = file.getResource();

        map.put(fullPath, resource);

        memory.add(map);
    }

    public void delete(String storedNAme){

    }
}
