package com.study.badrequest.domain.board.repository.query;

import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;

public interface BoardQueryRepository {

    public BoardListDto findBoardList(int size, Long lastIndex, String keyword, Category category, Topic topic);
}
