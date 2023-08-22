package com.study.badrequest.question.command.application;

import com.study.badrequest.question.command.application.dto.ModifyQuestionForm;
import com.study.badrequest.question.command.application.dto.UpdateQuestionWriterForm;

public interface QuestionModifyService {
    public Long modifyQuestion(ModifyQuestionForm form);

    Long updateQuestionWriter(UpdateQuestionWriterForm form);
}
