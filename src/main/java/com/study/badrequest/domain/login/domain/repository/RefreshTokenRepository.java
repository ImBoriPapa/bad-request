package com.study.badrequest.domain.login.domain.repository;

import com.study.badrequest.domain.login.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {

}
