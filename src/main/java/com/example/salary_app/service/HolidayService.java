package com.example.salary_app.service;

import java.util.Arrays;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HolidayService {

    private final RestClient restClient;

    public HolidayService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://date.nager.at")
                .build();
    }

    public List<LocalDate> getJapaneseHolidays(int year) {

        HolidayResponse[] holidays = restClient.get()
                .uri("/api/v3/PublicHolidays/{year}/JP", year)
                .retrieve()
                .body(HolidayResponse[].class);

        return Arrays.stream(holidays)
                .map(HolidayResponse::date)
                .toList();
    }

    public boolean isJapaneseHoliday(LocalDate date) {

        List<LocalDate> holidays = getJapaneseHolidays(date.getYear());

        return holidays.contains(date);
    }

    public record HolidayResponse(
            LocalDate date
    ) {
    }
}