package com.study.badrequest.dto.record;

import com.study.badrequest.domain.record.ActionStatus;
import com.study.badrequest.event.member.MemberEventDto;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class MemberRecordRequestMapper {

    public static MemberRecordRequest eventDtoToMemberRecordRequest(MemberEventDto.Create event) {
        return new MemberRecordRequest(
                ActionStatus.CREATED,
                event.getMember().getId(),
                event.getMember().getEmail(),
                event.getMember().getAuthority(),
                event.getMember().getIpAddress(),
                event.getSpecialNote(),
                event.getRecordTime()
        );
    }

    public static MemberRecordRequest eventDtoToMemberRecordRequest(MemberEventDto.Update event) {
        return new MemberRecordRequest(
                ActionStatus.UPDATED,
                event.getMember().getId(),
                event.getMember().getEmail(),
                event.getMember().getAuthority(),
                event.getMember().getIpAddress(),
                event.getSpecialNote(),
                event.getRecordTime()
        );
    }

    public static MemberRecordRequest eventDtoToMemberRecordRequest(MemberEventDto.Delete event) {
        return new MemberRecordRequest(
                ActionStatus.DELETED,
                event.getMember().getId(),
                event.getMember().getEmail(),
                event.getMember().getAuthority(),
                event.getMember().getIpAddress(),
                event.getSpecialNote(),
                event.getRecordTime()
        );
    }

    public static MemberRecordRequest eventDtoToMemberRecordRequest(MemberEventDto.Login event) {
        return new MemberRecordRequest(
                ActionStatus.LOGIN,
                event.getMember().getId(),
                event.getMember().getEmail(),
                event.getMember().getAuthority(),
                event.getMember().getIpAddress(),
                event.getSpecialNote(),
                event.getRecordTime()
        );
    }

    public static MemberRecordRequest eventDtoToMemberRecordRequest(MemberEventDto.Logout event) {
        return new MemberRecordRequest(
                ActionStatus.LOGOUT,
                event.getMember().getId(),
                event.getMember().getEmail(),
                event.getMember().getAuthority(),
                event.getMember().getIpAddress(),
                event.getSpecialNote(),
                event.getRecordTime()
        );
    }

    public static MemberRecordRequest eventDtoToMemberRecordRequest(MemberEventDto.IssueTemporaryPassword event) {
        return new MemberRecordRequest(
                ActionStatus.ISSUE_TEMPORARY_PASSWORD,
                event.getMember().getId(),
                event.getMember().getEmail(),
                event.getMember().getAuthority(),
                event.getMember().getIpAddress(),
                event.getSpecialNote(),
                event.getRecordTime()
        );
    }
}
