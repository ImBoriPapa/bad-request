package com.study.badrequest.question.command.domain.model;

import java.time.LocalDateTime;

public record InitializeAttachedImage(Long id,
                                      String path,
                                      String originalName,
                                      String storedName,
                                      String type,
                                      Long size,
                                      Boolean isTemp,
                                      LocalDateTime storedAt, Question question) {
}
