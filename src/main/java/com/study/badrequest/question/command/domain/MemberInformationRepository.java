package com.study.badrequest.question.command.domain;



import java.util.Optional;

public interface MemberInformationRepository {
    Optional<MemberInformation> findById(Long memberId);
}
