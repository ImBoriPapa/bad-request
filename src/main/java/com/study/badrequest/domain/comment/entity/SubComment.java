package com.study.badrequest.domain.comment.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class SubComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUB_COMMENT_ID")
    private Long id;
    private String text;

}
