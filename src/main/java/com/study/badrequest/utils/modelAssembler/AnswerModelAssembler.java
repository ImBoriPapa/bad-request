package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.answer.AnswerApiController;
import com.study.badrequest.dto.answer.AnswerResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AnswerModelAssembler {

    public EntityModel<AnswerResponse.Register> createAnswerRegisterModel(Long questionId, AnswerResponse.Register response) {
        List<Link> links = List.of(
                linkTo(methodOn(AnswerApiController.class).register(questionId, null, null, null)).withSelfRel(),
                linkTo(methodOn(AnswerApiController.class).modify(response.getId(), null, null)).withRel("modify"),
                linkTo(methodOn(AnswerApiController.class).delete(response.getId(), null)).withRel("delete")
        );
        return EntityModel.of(response, links);
    }
}
