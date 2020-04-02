package com.covid19.ashevtsov;

import com.covid19.ashevtsov.model.Covid19Statistic;
import com.covid19.ashevtsov.model.Covid19StatisticRequest;
import com.covid19.ashevtsov.service.Covid19Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class Covid19Controller {

    private final Covid19Service covid19Service;

    @GetMapping("/statistic/{value}")
    public ResponseEntity<Covid19Statistic> getStatistic(@PathVariable String value,
                                                         @RequestBody Covid19StatisticRequest request) {
        LocalDate toDate = getDate(value);
        return ResponseEntity.ok(covid19Service.getStatistic(toDate, request));
    }

    private LocalDate getDate(String value) {
        return value.matches("\\d+") ? LocalDate.now().plusDays(Long.parseLong(value)) : getDateFromString(value);
    }

    private LocalDate getDateFromString(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Не верный формат даты %s", date), e);
        }
    }

}
