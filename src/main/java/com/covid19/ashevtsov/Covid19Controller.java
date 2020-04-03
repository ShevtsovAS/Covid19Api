package com.covid19.ashevtsov;

import com.covid19.ashevtsov.model.Covid19Statistic;
import com.covid19.ashevtsov.model.Covid19StatisticRequest;
import com.covid19.ashevtsov.service.Covid19Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class Covid19Controller {

    private final Covid19Service covid19Service;

    @GetMapping("/statistic")
    public ResponseEntity<Covid19Statistic> getStatistic(@RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                                         @RequestParam(required = false) Long days,
                                                         @RequestParam List<Integer> lastDaysStatistic,
                                                         @RequestParam Integer recovery,
                                                         @RequestParam Integer died) {
        Covid19StatisticRequest request = getRequest(date, days, lastDaysStatistic, recovery, died);
        return ResponseEntity.ok(covid19Service.getStatistic(request));
    }

    @GetMapping("/statistic/csv")
    public void exportReport(@RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                             @RequestParam(required = false) Long days,
                             @RequestParam List<Integer> lastDaysStatistic,
                             @RequestParam Integer recovery,
                             @RequestParam Integer died,
                             HttpServletResponse response) {
        Covid19StatisticRequest request = getRequest(date, days, lastDaysStatistic, recovery, died);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", getFileName(request.getDate())));
        writeToResponse(response, covid19Service.getCSVStatistic(request));
    }

    private Covid19StatisticRequest getRequest(LocalDate date, Long days, List<Integer> lastDaysStatistic, Integer recovery, Integer died) {
        return Covid19StatisticRequest.builder()
                .date(date != null ? date : LocalDate.now().plusDays(days != null ? days : 15L))
                .lastDaysStatistic(lastDaysStatistic)
                .recovery(recovery)
                .died(died)
                .build();
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

}
