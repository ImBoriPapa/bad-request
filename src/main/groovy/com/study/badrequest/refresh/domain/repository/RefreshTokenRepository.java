package com.study.badrequest.refresh.domain.repository;

import com.study.badrequest.refresh.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,Long> {
}
