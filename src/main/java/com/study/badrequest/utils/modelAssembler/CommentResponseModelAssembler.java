package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.comment.CommentController;
import com.study.badrequest.api.comment.CommentQueryController;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.repository.dto.CommentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class CommentResponseModelAssembler  {

    private EntityModel<CommentResponse.Create> toModel(CommentResponse.Create target){

        return EntityModel.of(target);
    }

    public CollectionModel toCollectionModel(List<CommentDto> target) {

        return CollectionModel.of(target)
                .add(linkTo(methodOn(CommentQueryController.class).getComments(null)).withSelfRel());
    }

}
