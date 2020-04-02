package com.covid19.ashevtsov.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DayStatistic {
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonProperty("Дата")
    private LocalDate date;
    @JsonProperty("Всего зараженных")
    private Integer sickCount;
    @JsonProperty("Зараженных за день")
    private Integer sickIncrease;
    @JsonProperty("Процент прироста зараженных")
    private Double sickIncreasePercent;
    @JsonProperty("Выздоровело")
    private Integer recovery;
    @JsonProperty("Смертей")
    private Integer died;
}
