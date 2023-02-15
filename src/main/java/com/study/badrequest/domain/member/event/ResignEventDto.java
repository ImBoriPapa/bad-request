package com.study.badrequest.domain.member.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResignEventDto {
    private Long id;
    private String username;

}
