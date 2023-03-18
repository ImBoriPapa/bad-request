package com.study.badrequest.domain.mentor.repository;

import com.study.badrequest.domain.mentor.entity.BookingDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDayRepository extends JpaRepository<BookingDay,Long> {
}
