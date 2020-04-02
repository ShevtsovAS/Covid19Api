package com.covid19.ashevtsov;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppNumberUtils {
    public static double getPercentValue(int numPart, int num) {
        return num != 0 ? (double) numPart * 100 / num : 0;
    }

    public static int getNumberByPercent(int number, double percent) {
        return (int) (number * (percent / 100));
    }

    public static double getCoefficient(double percent) {
        return percent / 100 + 1;
    }

    public static int plusPercent(int num, double percent) {
        return (int) (num * getCoefficient(percent));
    }

    public static int minusPercent(int num, double percent) {
        return (int) (num * getCoefficient(percent * -1));
    }
}
