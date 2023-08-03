package com.study.badrequest.member.command.infra.redis;

import com.study.badrequest.member.command.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisRefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
