package com.study.badrequest.repository.member;


import com.study.badrequest.domain.member.AuthenticationMailInformation;
import org.springframework.data.repository.CrudRepository;



public interface AuthenticationMailInformationRepository extends CrudRepository<AuthenticationMailInformation,String> {

}
