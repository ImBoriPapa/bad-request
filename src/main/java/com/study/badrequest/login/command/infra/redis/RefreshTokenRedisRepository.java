package com.study.badrequest.login.command.infra.redis;

import com.study.badrequest.login.command.domain.RefreshTokenHash;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshTokenHash, String> {
}
