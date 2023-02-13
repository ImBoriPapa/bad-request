package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.comment.CommentController;
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
public class CommentResponseModelAssembler implements CustomEntityModelAssemblerSupport<Object, List<CommentDto>> {
    @Override
    public EntityModel toModel(Object o) {
        return null;
    }

    @Override
    public EntityModel<List<CommentDto>> toListModel(List<CommentDto> target) {

        return null;
    }

    public CollectionModel toCollectionModel(List<CommentDto> target) {

        return CollectionModel.of(target)
                .add(linkTo(methodOn(CommentController.class).getComments(null)).withSelfRel());
    }

}
