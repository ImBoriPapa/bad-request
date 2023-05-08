package com.study.badrequest.utils.converter;


import com.study.badrequest.domain.board.Category;
import org.springframework.core.convert.converter.Converter;



public class BindingParamToCategory implements Converter<String, Category> {

    @Override
    public Category convert(String source) {
        return Category.convert(source);
    }
}


