package com.study.badrequest.event.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRecordEventDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        Long memberId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Update {
        Long memberId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Delete {
        Long memberId;
    }
}
