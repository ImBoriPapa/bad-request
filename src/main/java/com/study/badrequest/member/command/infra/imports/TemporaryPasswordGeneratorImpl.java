package com.study.badrequest.member.command.infra.imports;

import com.study.badrequest.member.command.domain.imports.TemporaryPasswordGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TemporaryPasswordGeneratorImpl implements TemporaryPasswordGenerator {

    @Override
    public String generator() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
