package com.study.badrequest.domain.mentor.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "BOOKING_DAY_ID")
    private BookingDay bookingDay;

    private BookingTime(LocalTime startTime, LocalTime endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public static BookingTime createAvailableBookingTime(LocalTime startTime, LocalTime endTime) {
        return new BookingTime(startTime, endTime, true);
    }

    public void setBookingDay(BookingDay bookingDay) {

        if (this.bookingDay != null) {
            this.bookingDay.getBookingTimeList().remove(this);
        }

        this.bookingDay = bookingDay;

        bookingDay.getBookingTimeList().add(this);
    }
}
