package com.covid19.ashevtsov;

import com.covid19.ashevtsov.model.Covid19Statistic;
import com.covid19.ashevtsov.model.Covid19StatisticRequest;
import com.covid19.ashevtsov.service.Covid19Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping("/statistic/csv/{value}")
    public void exportReport(@PathVariable String value,
                             @RequestBody Covid19StatisticRequest request,
                             HttpServletResponse response) {
        LocalDate toDate = getDate(value);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", getFileName(toDate)));
        writeToResponse(response, covid19Service.getCSVStatistic(toDate, request));
    }

    private void writeToResponse(HttpServletResponse response, byte[] csvStatistic) {
        try {
            response.getOutputStream().write(csvStatistic);
        } catch (IOException e) {
            log.error("Error occurred during writing report to response", e);
            throw new RuntimeException("Error occurred during writing report to response", e);
        }
    }

    private String getFileName(LocalDate toDate) {
        return String.format("Covid19_statistic_%s.csv", toDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy")));
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
