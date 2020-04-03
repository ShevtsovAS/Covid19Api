package com.covid19.ashevtsov.service;

import com.covid19.ashevtsov.model.Covid19Statistic;
import com.covid19.ashevtsov.model.Covid19StatisticRequest;

public interface Covid19Service {
    Covid19Statistic getStatistic(Covid19StatisticRequest request);

    byte[] getCSVStatistic(Covid19StatisticRequest request);
}
