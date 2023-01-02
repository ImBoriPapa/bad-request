package com.study.badrequest.login.domain.repository;

import com.study.badrequest.login.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,Long> {
}
