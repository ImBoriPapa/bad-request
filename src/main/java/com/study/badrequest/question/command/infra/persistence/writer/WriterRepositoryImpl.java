package com.study.badrequest.question.command.infra.persistence.writer;

import com.study.badrequest.question.command.domain.model.Writer;
import com.study.badrequest.question.command.domain.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WriterRepositoryImpl implements WriterRepository {

    private final WriterJpaRepository writerJpaRepository;

    @Override
    public Writer save(Writer writer) {
        return writerJpaRepository.save(WriterEntity.fromModel(writer)).toModel();
    }

    @Override
    public Optional<Writer> findById(Long id) {
        return writerJpaRepository.findById(id).map(WriterEntity::toModel);
    }

    @Override
    public Optional<Writer> findByMemberId(Long memberId) {
        return writerJpaRepository.findByMemberId(memberId).map(WriterEntity::toModel);
    }
}
