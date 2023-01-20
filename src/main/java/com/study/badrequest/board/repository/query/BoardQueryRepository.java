package com.study.badrequest.board.repository.query;

import com.study.badrequest.board.entity.Category;
import com.study.badrequest.board.entity.Topic;

public interface BoardQueryRepository {

    public BoardListDto findBoardList(int size, Long lastIndex, String keyword, Category category, Topic topic);
}
