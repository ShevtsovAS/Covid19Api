package com.covid19.ashevtsov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Covid19Statistic {
    @JsonProperty("Средний процент прироста зараженных")
    private Double averageSickIncrease;
    @JsonProperty("Средний процент смертности")
    private Double averageMortality;
    @JsonProperty("Средний процент выздоровлений")
    private Double averageRecovery;
    @JsonProperty("Статистика до сегодняшнего дня")
    private List<DayStatistic> currentStatistics;
    @JsonProperty("Возможная статистика на будущее")
    private List<DayStatistic> prospectiveStatistics;
}
