package com.covid19.ashevtsov.service;

import com.covid19.ashevtsov.model.Covid19Statistic;
import com.covid19.ashevtsov.model.Covid19StatisticRequest;
import com.covid19.ashevtsov.model.DayStatistic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static com.covid19.ashevtsov.AppNumberUtils.*;
import static java.util.stream.Collectors.toList;
import static org.decimal4j.util.DoubleRounder.round;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Service
@Scope(SCOPE_PROTOTYPE)
public class Covid19ServiceImpl implements Covid19Service {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private double averageRecovery;
    private double averageMortality;
    private double averageSickIncrease;

    @Value("${percentRounding.precision:2}")
    private int precision;
    @Value("${percentRounding.enabled}")
    private boolean percentRoundingEnabled;

    @Override
    public Covid19Statistic getStatistic(Covid19StatisticRequest request) {

        averageRecovery = getAverageRecovery(request);
        averageMortality = getAverageMortality(request);
        List<DayStatistic> currentStatistic = getCurrentStatistic(request);
        averageSickIncrease = getAverageSickIncrease(currentStatistic);
        List<DayStatistic> prospectiveStatistics = getProspectiveStatistics(request);

        Covid19Statistic statistic = Covid19Statistic.builder()
                .averageSickIncrease(averageSickIncrease)
                .averageMortality(averageMortality)
                .averageRecovery(averageRecovery)
                .currentStatistics(currentStatistic)
                .prospectiveStatistics(prospectiveStatistics)
                .build();

        if (percentRoundingEnabled) {
            roundDoubleValues(statistic);
        }

        return statistic;
    }

    @Override
    public byte[] getCSVStatistic(Covid19StatisticRequest request) {
        Covid19Statistic statistic = getStatistic(request);
        return convertToCSV(statistic);
    }

    private byte[] convertToCSV(Covid19Statistic statistic) {
        String header = String.join(",", "Дата", "Заразилось", "Выздоровело", "Смертей");
        List<String> data = Stream.concat(statistic.getCurrentStatistics().stream(), statistic.getProspectiveStatistics().stream())
                .map(this::getCSVString)
                .collect(toList());
        List<String> csv = new ArrayList<>();
        csv.add(header);
        csv.addAll(data);
        return String.join("\n", csv).getBytes(StandardCharsets.UTF_8);
    }

    private String getCSVString(DayStatistic dayStatistic) {
        return String.format("%s,%d,%d,%d",
                dayStatistic.getDate().format(DATE_FORMAT),
                dayStatistic.getSickCount(),
                dayStatistic.getRecovery(),
                dayStatistic.getDied());
    }

    private void roundDoubleValues(Covid19Statistic statistic) {
        statistic.setAverageSickIncrease(round(statistic.getAverageSickIncrease(), precision));
        statistic.setAverageMortality(round(statistic.getAverageMortality(), precision));
        statistic.setAverageRecovery(round(statistic.getAverageRecovery(), precision));
        statistic.getCurrentStatistics().forEach(dayStatistic -> dayStatistic.setSickIncreasePercent(round(dayStatistic.getSickIncreasePercent(), precision)));
        statistic.getProspectiveStatistics().forEach(dayStatistic -> dayStatistic.setSickIncreasePercent(round(dayStatistic.getSickIncreasePercent(), precision)));
    }

    private List<DayStatistic> getProspectiveStatistics(Covid19StatisticRequest request) {
        List<DayStatistic> result = new ArrayList<>();
        int sickCount = request.getLastDaysStatistic().get(request.getLastDaysStatistic().size() - 1);
        LocalDate date = LocalDate.now();

        while (date.isBefore(request.getDate())) {
            date = date.plusDays(1);
            int nextSickCount = plusPercent(sickCount, averageSickIncrease);
            int sickIncrease = nextSickCount - sickCount;
            int recovery = getNumberByPercent(nextSickCount, averageRecovery);
            int died = getNumberByPercent(nextSickCount, averageMortality);
            result.add(DayStatistic.builder()
                    .date(date)
                    .sickCount(nextSickCount)
                    .sickIncrease(sickIncrease)
                    .sickIncreasePercent(averageSickIncrease)
                    .recovery(recovery)
                    .died(died)
                    .build());
            sickCount = nextSickCount;
        }

        return result;
    }

    private double getAverageSickIncrease(List<DayStatistic> currentStatistic) {
        return currentStatistic.stream().mapToDouble(DayStatistic::getSickIncreasePercent).sum() / currentStatistic.size();
    }

    private double getAverageRecovery(Covid19StatisticRequest request) {
        int currentDaySickCount = request.getLastDaysStatistic().get(request.getLastDaysStatistic().size() - 1);
        int currentDayRecovery = request.getRecovery();
        return getPercentValue(currentDayRecovery, currentDaySickCount);
    }

    private double getAverageMortality(Covid19StatisticRequest request) {
        int currentDaySickCount = request.getLastDaysStatistic().get(request.getLastDaysStatistic().size() - 1);
        int currentDayMortality = request.getDied();
        return getPercentValue(currentDayMortality, currentDaySickCount);
    }

    private List<DayStatistic> getCurrentStatistic(Covid19StatisticRequest request) {
        Iterator<Integer> iterator = request.getLastDaysStatistic().iterator();
        int sickCount = iterator.next();
        LocalDate date = LocalDate.now().minusDays(request.getLastDaysStatistic().size() - 1);
        List<DayStatistic> result = new ArrayList<>();
        while (iterator.hasNext()) {
            date = date.plusDays(1);
            int nextSickCount = iterator.next();
            int sickIncrease = nextSickCount - sickCount;
            double sickIncreasePercent = getPercentValue(sickIncrease, sickCount);
            int recovery = getNumberByPercent(nextSickCount, averageRecovery);
            int died = getNumberByPercent(nextSickCount, averageMortality);
            result.add(DayStatistic.builder()
                    .date(date)
                    .sickCount(nextSickCount)
                    .sickIncrease(sickIncrease)
                    .sickIncreasePercent(sickIncreasePercent)
                    .recovery(recovery)
                    .died(died)
                    .build());
            sickCount = nextSickCount;
        }
        return result;
    }
}
