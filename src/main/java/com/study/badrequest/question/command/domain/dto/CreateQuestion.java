package com.study.badrequest.question.command.domain.dto;

import com.study.badrequest.question.command.domain.model.*;

public record CreateQuestion(
        Writer writer,
        String title,
        String contents,
        CountOfRecommend countOfRecommend,
        CountOfUnRecommend countOfUnRecommend,
        CountOfView countOfView,
        CountOfAnswer countOfAnswer) {

}
