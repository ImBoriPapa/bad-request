package com.study.badrequest.utils.converter;

import com.study.badrequest.question.command.domain.values.QuestionSortType;
import org.springframework.core.convert.converter.Converter;

public class BindingParamToQuestionSortCriteria implements Converter<String, QuestionSortType> {

    @Override
    public QuestionSortType convert(String source) {
        return QuestionSortType.convert(source);
    }
}
