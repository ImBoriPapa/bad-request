package com.study.badrequest.domain.login.repository;

import com.study.badrequest.domain.login.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {

}
