package com.study.badrequest.api.booking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class BookingController {
    @PostMapping("/api/v1/booking")
    public void createBooking(@RequestBody BookingRequest booking) {
        log.info("Counselor= {}", booking.counselorId);
        for (BookingDay day : booking.bookingDayList) {
            log.info("Booking Day= {}", day.date);
            log.info("Booking Available= {}", day.available);
            for (BookingTime time : day.bookingTimeList) {
                log.info("Booking start= {}", time.start);
                log.info("Booking end= {}", time.end);
                log.info("Booking available= {}", time.available);
            }
        }
    }

    @NoArgsConstructor
    @Getter
    static class BookingRequest {
        private Long counselorId;
        private List<BookingDay> bookingDayList = new ArrayList<>();

    }

    @NoArgsConstructor
    @Getter
    static class BookingDay {
        private LocalDate date;
        private Boolean available;
        private List<BookingTime> bookingTimeList = new ArrayList<>();
    }

    @NoArgsConstructor
    @Getter
    static class BookingTime {
        private LocalTime start;
        private LocalTime end;
        private Boolean available;
    }
}
