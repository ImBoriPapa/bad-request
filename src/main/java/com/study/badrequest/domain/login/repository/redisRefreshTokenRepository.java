package com.study.badrequest.domain.login.repository;

import com.study.badrequest.domain.login.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface redisRefreshTokenRepository extends CrudRepository<RefreshToken,String> {

}
