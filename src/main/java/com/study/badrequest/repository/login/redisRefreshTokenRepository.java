package com.study.badrequest.repository.login;


import com.study.badrequest.domain.login.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface redisRefreshTokenRepository extends CrudRepository<RefreshToken,String> {

}
