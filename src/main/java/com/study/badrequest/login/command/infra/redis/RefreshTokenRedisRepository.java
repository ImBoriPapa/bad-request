package com.study.badrequest.login.command.infra.redis;

import com.study.badrequest.login.command.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}
