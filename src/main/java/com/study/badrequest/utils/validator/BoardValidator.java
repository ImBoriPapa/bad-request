package com.study.badrequest.utils.validator;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.dto.board.BoardRequest;
import com.study.badrequest.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardValidator {

    public final BoardRepository boardRepository;

    @CustomLogTracer
    public void validateUpdateForm(BoardRequest.Update form){

    }
}
