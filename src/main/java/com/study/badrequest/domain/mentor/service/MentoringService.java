package com.study.badrequest.domain.mentor.service;

import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.mentor.dto.MentorRequest;
import com.study.badrequest.domain.mentor.entity.BookingDay;
import com.study.badrequest.domain.mentor.entity.BookingTime;
import com.study.badrequest.domain.mentor.entity.Mentor;
import com.study.badrequest.domain.mentor.repository.BookingDayRepository;
import com.study.badrequest.domain.mentor.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.study.badrequest.domain.mentor.entity.BookingDay.createAvailableDate;
import static com.study.badrequest.domain.mentor.entity.BookingTime.createAvailableBookingTime;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MentoringService {
    private final MemberRepository memberRepository;
    private final MentorRepository mentorRepository;
    private final BookingDayRepository bookingDayRepository;

    public Mentor register(Long memberId, MentorRequest.Register request) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원을 찾응 수 없습니다."));

        Mentor mentor = Mentor.createMentor()
                .title(request.getTitle())
                .introduce(request.getIntroduce())
                .job(request.getJob())
                .currentCompany(request.getCurrentCompany())
                .career(request.getCareer())
                .member(member)
                .build();

        Mentor save = mentorRepository.save(mentor);

        return mentor;
    }

    public Long update(Long mentorId, MentorRequest.Update request) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        mentor.updateTitle(request.getTitle());
        mentor.updateIntroduce(request.getIntroduce());
        mentor.updateJob(request.getJob());
        mentor.updateCurrentCompany(request.getCurrentCompany());
        mentor.updateCareer(request.getCareer());

        return mentor.getId();
    }

    public void setScheduling(Long mentorId, MentorRequest.Scheduling request) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        bookingDayRepository.saveAll(getBookingDayList(request.getBookingDayList(), mentor));
    }
    
    private List<BookingDay> getBookingDayList(List<MentorRequest.AvailableDayRequest> requests, Mentor mentor) {
        return requests.stream().map(day -> createAvailableDate(day.getDate(), getBookingTimeList(day.getAvailableTimeRequestList()), mentor)).collect(Collectors.toList());
    }

    private List<BookingTime> getBookingTimeList(List<MentorRequest.AvailableTimeRequest> request) {
        return request.stream()
                .map(time -> createAvailableBookingTime(time.getStart(), time.getEnd()))
                .collect(Collectors.toList());
    }

    public void updateScheduling(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException(""));

    }

    public void cancelRegistration(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException(""));
        mentor.cancelRegistration();
    }
}
