package com.study.badrequest.domain.login.service;

import com.study.badrequest.domain.login.entity.RefreshToken;
import com.study.badrequest.domain.login.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public Iterable<RefreshToken> findAll(){
        return refreshTokenRepository.findAll();
    }
}
