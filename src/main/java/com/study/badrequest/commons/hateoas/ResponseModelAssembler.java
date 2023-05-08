package com.study.badrequest.commons.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.List;

public abstract class ResponseModelAssembler {

    protected  <T> EntityModel<T> createEntityModel(T result, Link link) {
        return EntityModel.of(result).add(link);
    }
    protected <T> EntityModel<T> createEntityModel(T result, List<Link> links) {
        return EntityModel.of(result).add(links);
    }


}
