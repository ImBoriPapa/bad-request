package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.member.MemberQueryApiController;
import com.study.badrequest.api.question.QuestionApiController;
import com.study.badrequest.api.question.QuestionQueryApiController;
import com.study.badrequest.domain.question.QuestionSort;
import com.study.badrequest.domain.recommendation.RecommendationKind;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.repository.question.query.QuestionDetail;
import com.study.badrequest.repository.question.query.TagDto;
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

    public EntityModel<QuestionDetail> createDetailModel(QuestionDetail questionDetail) {


        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(questionDetail.getId(), null, null, null)).withSelfRel());

        if (questionDetail.getIsQuestioner()) {
            links.add(linkTo(methodOn(QuestionApiController.class).modify(questionDetail.getId(), null, null)).withRel("modify"));
            links.add(linkTo(methodOn(QuestionApiController.class).delete(questionDetail.getId(), null)).withRel("delete"));
        } else {
            links.add(linkTo(methodOn(QuestionApiController.class).recommendation(questionDetail.getId(), true)).withRel("recommendation"));
            links.add(linkTo(methodOn(QuestionApiController.class).recommendation(questionDetail.getId(), false)).withRel("un-recommendation"));
        }

        questionDetail.getTag().forEach(tag -> tag.add(
                getHashTagLink(tag)
        ));

        return EntityModel.of(questionDetail, links);
    }

    public EntityModel<QuestionResponse.Create> createCreateModel(QuestionResponse.Create response) {
        List<Link> links = List.of(
                linkTo(methodOn(QuestionApiController.class).create(null, null, null)).withSelfRel(),
                linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(response.getId(), null, null, null)).withRel("detail"),
                linkTo(methodOn(QuestionApiController.class).modify(response.getId(), null, null)).withRel("modify"),
                linkTo(methodOn(QuestionApiController.class).delete(response.getId(), null)).withRel("delete")
        );
        return EntityModel.of(response, links);
    }

    public EntityModel<QuestionResponse.Modify> createModifyModel(QuestionResponse.Modify response) {
        List<Link> links = List.of(
                linkTo(methodOn(QuestionApiController.class).modify(response.getId(), null, null)).withSelfRel()
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

        addResultsLink(result);
        return EntityModel.of(result, resultLinks);
    }

    private void addResultsLink(QuestionListResult result) {
        result.getResults().forEach(dto -> {

            dto.add(
                    linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(dto.getId(), null, null, null)).withRel("to detail"),
                    linkTo(methodOn(MemberQueryApiController.class).getProfile(dto.getQuestioner().getId(), null)).withRel("to Questioner Profile")
            );
            dto.getTags().forEach(tagDto -> tagDto.add(getHashTagLink(tagDto)));

        });
    }

    private Link getHashTagLink(TagDto tagDto) {
        String tag = HashTagUtils.hashTagToTag(tagDto.getTagName());
        return linkTo(methodOn(QuestionQueryApiController.class).getQuestionsByTag(tag)).withRel("find by tagged");
    }

    private void addSortLink(QuestionSearchCondition condition, List<Link> links) {

        if (condition.getSort() != QuestionSort.NEW_EAST) {
            Link sortByNew = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                    .withRel("sort by new-east");
            links.add(sortByNew);
        }

        if (condition.getSort() != QuestionSort.VIEW) {
            Link sortByView = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                    .slash("?sort=" + QuestionSort.VIEW.toString().toLowerCase())
                    .withRel("sort by view");
            links.add(sortByView);

        }
        if (condition.getSort() != QuestionSort.RECOMMEND) {
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
            String param = "lastOfData=" + result.getLastOfData();
            Link nextData = linkTo(methodOn(QuestionQueryApiController.class)
                    .getQuestions(condition))
                    .slash("?" + param)
                    .withRel("NEXT DATA");

            links.add(nextData);
        }
    }
}
