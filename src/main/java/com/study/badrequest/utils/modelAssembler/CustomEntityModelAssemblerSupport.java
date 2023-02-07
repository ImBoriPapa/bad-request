package com.study.badrequest.utils.modelAssembler;


import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;


public interface CustomEntityModelAssemblerSupport<T, C> {

    EntityModel<T> toModel(T t);
    EntityModel<C> toListModel(C target);
    CollectionModel<C> toCollectionModel(C target);

}
