package com.covid19.ashevtsov.service;

import com.covid19.ashevtsov.model.Covid19Statistic;
import com.covid19.ashevtsov.model.Covid19StatisticRequest;

import java.time.LocalDate;

public interface Covid19Service {
    Covid19Statistic getStatistic(LocalDate toDate, Covid19StatisticRequest request);
}
