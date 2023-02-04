package com.study.badrequest.utils.converter;

import com.study.badrequest.domain.board.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;


@Slf4j
public class BindingParamToCategory implements Converter<String, Category> {

    @Override
    public Category convert(String source) {

        return Category.convert(source);
    }
}


