package com.study.badrequest.outbox.command.domain;

import java.util.List;

public interface OutBoxRepository {

    OutBoxMessage save(OutBoxMessage outBoxMessage);

    List<OutBoxMessage> findAll();

    void delete(OutBoxMessage message);
}
