package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.Writer;
import com.study.badrequest.question.command.domain.WriterRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriterJpaRepository extends JpaRepository<Writer,Long>, WriterRepository {

}
