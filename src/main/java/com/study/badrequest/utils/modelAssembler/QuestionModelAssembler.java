package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.member.MemberQueryApiController;
import com.study.badrequest.api.question.QuestionQueryApiController;
import com.study.badrequest.commons.hateoas.ResponseModelAssembler;
import com.study.badrequest.domain.question.QuestionSort;
import com.study.badrequest.repository.question.query.QuestionListResult;
import com.study.badrequest.repository.question.query.QuestionSearchCondition;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class QuestionModelAssembler extends ResponseModelAssembler {

        public EntityModel<QuestionListResult> getQuestionListModel(QuestionListResult result, QuestionSearchCondition condition) {
            ArrayList<Link> links = new ArrayList<>();

            Link selfRel = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition)).withSelfRel();
            links.add(selfRel);

            setSortLink(result, condition, links);

            if (condition.getSize() == null) {
                Link increaseOrDecreaseDataSize = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                        .slash("?size=" + (result.getSize() + 10))
                        .withRel("increase or decrease data size");
                links.add(increaseOrDecreaseDataSize);
            }

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
            if (condition.getIsAnswered() == null) {
                Link filterByIsAnswered = linkTo(methodOn(QuestionQueryApiController.class).getQuestions(condition))
                        .slash("?isAnswered=" + true)
                        .withRel("filter by isAnswered");
                links.add(filterByIsAnswered);

            }

            result.add(links);

            result.getResults().forEach(dto -> {
                Link toDetail = linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(dto.getId(), null)).withRel("to detail");
                Link toProfile = linkTo(methodOn(MemberQueryApiController.class).getProfile(dto.getQuestioner().getId(), null)).withRel("to Questioner Profile");

                dto.add(toDetail, toProfile);

                dto.getHashTag().forEach(hashTagDto -> {
                    String tag = hashTagDto.getTagName().replace("#", "");
                    Link findByTagName = linkTo(methodOn(QuestionQueryApiController.class).getQuestionsByTag(tag)).withRel("find by tagged");
                    dto.add(findByTagName);
                });
            });

            return createEntityModel(result,links);
        }

    private void setSortLink(QuestionListResult result, QuestionSearchCondition condition, ArrayList<Link> links) {
        if (result.getHasNext()) {
            String param = "";
            switch (result.getSortBy()) {
                case NEW_EAST:
                    param = "lastIndex=" + result.getLastOfIndex();
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
