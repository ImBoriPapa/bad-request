package com.study.badrequest.domain.mentor.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENTOR_ID")
    private Mentor mentor;

    @OneToMany(mappedBy = "bookingDay", cascade = CascadeType.ALL)
    private List<BookingTime> bookingTimeList = new ArrayList<>();

    protected BookingDay(LocalDate date, boolean available, Mentor mentor) {
        this.date = date;
        this.available = available;
        this.mentor = mentor;
    }

    public static BookingDay createAvailableDate(LocalDate date, List<BookingTime> bookingTimes, Mentor mentor) {
        BookingDay day = new BookingDay(date, true, mentor);

        bookingTimes.forEach(time -> {
            time.setBookingDay(day);
        });

        return day;
    }
}
