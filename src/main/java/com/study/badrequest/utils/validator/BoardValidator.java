package com.study.badrequest.utils.validator;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.commons.exception.custom_exception.BoardException;
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
        if(form.getMemberId() == null){
            throw new BoardException(CustomStatus.NOTFOUND_MEMBER);
        }
    }
}
