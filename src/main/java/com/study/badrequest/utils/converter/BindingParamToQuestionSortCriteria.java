package com.study.badrequest.utils.converter;

import com.study.badrequest.domain.question.QuestionSort;
import org.springframework.core.convert.converter.Converter;

public class BindingParamToQuestionSortCriteria implements Converter<String, QuestionSort> {

    @Override
    public QuestionSort convert(String source) {
        return QuestionSort.convert(source);
    }
}
