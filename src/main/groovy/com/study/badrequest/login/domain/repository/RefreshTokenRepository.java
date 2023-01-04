package com.study.badrequest.login.domain.repository;

import com.study.badrequest.login.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {


}
