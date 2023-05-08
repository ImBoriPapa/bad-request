package com.study.badrequest.dto.board;


import com.study.badrequest.domain.board.Category;
import com.study.badrequest.domain.board.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter  //파라미터 바인딩을 위해 필요
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchCondition {
    private Integer size;
    private Long lastIndex;
    private String title;
    private Category category;
    private Topic topic;
    private String nickname;
    private Long memberId;
}
