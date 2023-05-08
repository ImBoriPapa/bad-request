package com.study.badrequest.utils.converter;


import com.study.badrequest.domain.board.Topic;
import org.springframework.core.convert.converter.Converter;

public class BindingParamToTopic implements Converter<String, Topic> {

    @Override
    public Topic convert(String source) {
        return Topic.convert(source);
    }
}
