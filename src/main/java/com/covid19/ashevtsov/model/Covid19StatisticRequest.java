package com.covid19.ashevtsov.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Covid19StatisticRequest {
    private LocalDate date;
    private List<Integer> lastDaysStatistic;
    private Integer recovery;
    private Integer died;
}
