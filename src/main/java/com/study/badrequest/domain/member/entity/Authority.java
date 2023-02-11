package com.study.badrequest.domain.member.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum Authority {
    MEMBER("ROLL_MEMBER"),
    TEACHER("ROLL_MEMBER,ROLL_TEACHER"),
    ADMIN("ROLL_MEMBER,ROLL_TEACHER,ROLL_ADMIN");

    private final String roll;

    Authority(String roll) {
        this.roll = roll;
    }

    public List<String> getRoleList() {
        ArrayList<String> list = new ArrayList<>();
        Arrays.stream(this.roll.split(","))
                .forEach(list::add);
        return list;
    }
}
