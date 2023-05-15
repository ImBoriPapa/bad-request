package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.member.MemberQueryApiController;
import com.study.badrequest.api.question.QuestionApiController;
import com.study.badrequest.api.question.QuestionQueryApiController;
import com.study.badrequest.domain.question.QuestionSort;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.repository.question.query.HashTagDto;
import com.study.badrequest.repository.question.query.QuestionListResult;
import com.study.badrequest.repository.question.query.QuestionSearchCondition;
import com.study.badrequest.utils.hash_tag.HashTagUtils;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class QuestionModelAssembler {

    public EntityModel<QuestionResponse.Create> createCreateModel(QuestionResponse.Create response) {
        List<Link> links = List.of(
                linkTo(methodOn(QuestionApiController.class).create(null, null, null)).withSelfRel()
        );
        return EntityModel.of(response, links);
    }

    public EntityModel<QuestionListResult> getQuestionListModel(QuestionListResult result, QuestionSearchCondition condition) {
        List<Link> resultLinks = new ArrayList<>();

        //default self rel
        resultLinks.add(linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition)).withSelfRel());

        addNextLink(result, condition, resultLinks);

        addSizeLink(result, condition, resultLinks);

        addSortLink(condition, resultLinks);

        addIsAnsweredLink(condition, resultLinks);

        addResultsLink(result);
        return EntityModel.of(result, resultLinks);
    }

    private void addResultsLink(QuestionListResult result) {
        result.getResults().forEach(dto -> {

            dto.add(
                    linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(dto.getId(),null,null, null)).withRel("to detail"),
                    linkTo(methodOn(MemberQueryApiController.class).getProfile(dto.getQuestioner().getId(), null)).withRel("to Questioner Profile")
            );
            dto.getHashTag().forEach(hashTagDto -> dto.add(getHashTagLink(hashTagDto)));
        });
    }

    private Link getHashTagLink(HashTagDto hashTagDto) {
        String tag = HashTagUtils.hashTagToTag(hashTagDto.getHashTagName());
        return linkTo(methodOn(QuestionQueryApiController.class).getQuestionsByTag(tag)).withRel("find by tagged");
    }

    private void addIsAnsweredLink(QuestionSearchCondition condition, List<Link> links) {

        if (condition.getIsAnswered() == null) {
            Link filterByIsAnswered = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                    .slash("?isAnswered=" + true)
                    .withRel("filter by isAnswered");
            links.add(filterByIsAnswered);
        }
    }

    private void addSortLink(QuestionSearchCondition condition, List<Link> links) {

        if (condition.getLastOfView() != null || condition.getLastOfRecommend() != null) {
            Link sortByNew = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition)).withRel("sort by new-east");
            links.add(sortByNew);
        }

        if (condition.getLastOfView() == null) {
            Link sortByView = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                    .slash("?sort=" + QuestionSort.VIEW.toString().toLowerCase())
                    .withRel("sort by view");
            links.add(sortByView);

        }
        if (condition.getLastOfRecommend() == null) {
            Link sortByRecommend = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                    .slash("?sort=" + QuestionSort.RECOMMEND.toString().toLowerCase())
                    .withRel("sort by recommend");
            links.add(sortByRecommend);

        }
    }

    private void addSizeLink(QuestionListResult result, QuestionSearchCondition condition, List<Link> links) {
        if (condition.getSize() == null) {
            Link increaseOrDecreaseDataSize = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                    .slash("?size=" + (result.getSize() + 10))
                    .withRel("increase or decrease data size");
            links.add(increaseOrDecreaseDataSize);
        }
    }

    private void addNextLink(QuestionListResult result, QuestionSearchCondition condition, List<Link> links) {

        if (result.getHasNext()) {
            String param = "";
            switch (result.getSortBy()) {
                case NEW_EAST:
                    param = "lastOfIndex=" + result.getLastOfIndex();
                    break;
                case VIEW:
                    param = "lastOfView=" + result.getLastOfView();
                    break;
                case RECOMMEND:
                    param = "lastOfRecommend=" + result.getLastOfRecommend();
                    break;
            }

            Link nextData = linkTo(methodOn(QuestionQueryApiController.class)
                    .getQuestions(condition))
                    .slash("?" + param)
                    .withRel("NEXT DATA");

            links.add(nextData);
        }
    }
}
