package org.bitbucket.paidaki.pertcharts.application.util;

import javafx.util.StringConverter;
import org.bitbucket.paidaki.pertcharts.application.Activity;
import org.bitbucket.paidaki.pertcharts.gui.tabs.pertcharttab.chart.DraggableActivityNode;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static final DateTimeFormatter DATE_FORMATTER1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATE_FORMATTER2 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public static final StringConverter<LocalDate> DATE_STRING_CONVERTER = new StringConverter<LocalDate>() {
        @Override
        public String toString(LocalDate date) {
            return Util.dateToString(date, DATE_FORMATTER2);
        }

        @Override
        public LocalDate fromString(String string) {
            LocalDate date = Util.validateDate(string, DATE_FORMATTER2);
            return date == null ? LocalDate.MIN : date;
        }
    };

    public static final StringConverter<Number> NUMBER_STRING_CONVERTER = new StringConverter<Number>() {
        @Override
        public String toString(Number number) {
            return String.valueOf(number.intValue());
        }

        @Override
        public Number fromString(String string) {
            if (string.isEmpty())
                return 0;
            else
                return Double.parseDouble(string);
        }
    };

    public static boolean validateDate(LocalDate date) {
        return !(date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY));
    }

    public static boolean validateBeforeDate(LocalDate date1, LocalDate date2) {
        return validateDate(date1) && (date1.isBefore(date2) || date1.isEqual(date2));
    }

    public static boolean validateAfterDate(LocalDate date1, LocalDate date2) {
        return validateDate(date1) && (date1.isAfter(date2) || date1.isEqual(date2));
    }

    public static LocalDate validateDate(String input) {
        return validateDate(input, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDate validateDate(String input, DateTimeFormatter formatter) {
        if (!input.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(input, formatter);
                if (validateDate(date)) {
                    return date;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String dateToString(LocalDate date) {
        return String.valueOf(date);
    }

    public static String dateToString(LocalDate date, DateTimeFormatter formatter) {
        if (date != null) {
            return formatter.format(date);
        }
        return null;
    }

    public static LocalDate firstWorkingDate(LocalDate date) {
        return validateDate(date) ? date : firstWorkingDate(date.plusDays(1));
    }

    public static int getWorkingDays(LocalDate start, LocalDate end) {
        int workingDays = 1;

        while (start.isBefore(end)) {
            if (validateDate(start)) {
                workingDays++;
            }
            start = start.plusDays(1);
        }
        return workingDays;
    }

    public static LocalDate addWorkingDays(LocalDate date, int days) {
        while (days > 0) {
            date = date.plusDays(1);
            if (validateDate(date)) {
                days--;
            }
        }
        return date;
    }

    public static LocalDate subtractWorkingDays(LocalDate date, int days) {
        while (days > 0) {
            date = date.minusDays(1);
            if (validateDate(date)) {
                days--;
            }
        }
        return date;
    }

    public static boolean isBetweenDates(LocalDate date, LocalDate start, LocalDate end) {
        return date.isAfter(start) && date.isBefore(end);
    }

    public static LocalDate maxEndDate(List<Activity> list) {
        if (list.isEmpty()) {
            return LocalDate.MIN;
        }
        LocalDate maxDate = list.get(0).getEndDate();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getEndDate().isAfter(maxDate)) {
                maxDate = list.get(i).getEndDate();
            }
        }
        return maxDate;
    }

    public static LocalDate minStartDate(List<Activity> successors) {
        if (successors.isEmpty()) {
            return LocalDate.MAX;
        }
        LocalDate minDate = successors.get(0).getStartDate();
        for (int i = 1; i < successors.size(); i++) {
            if (successors.get(i).getStartDate().isBefore(minDate)) {
                minDate = successors.get(i).getStartDate();
            }
        }
        return minDate;
    }

    public static int maxEarliestFinish(List<Activity> predecessors) {
        int max = 0;
        for (Activity a : predecessors) {
            if (a.getEfDays() > max) {
                max = a.getEfDays();
            }
        }
        return max;
    }

    public static int minLatestStart(List<Activity> successors) {
        if (successors.isEmpty()) {
            return 0;
        }
        int min = successors.get(0).getLsDays();
        for (int i = 1; i < successors.size(); i++) {
            if (successors.get(i).getLsDays() < min) {
                min = successors.get(i).getLsDays();
            }
        }
        return min;
    }

    public static int maxEFLeafs(List<Activity> activities) {
        int maxEarliestFinish = 0;
        for (Activity a : activities) {
            if (a.getSuccessors().isEmpty()) {
                if (a.getEfDays() > maxEarliestFinish) {
                    maxEarliestFinish = a.getEfDays();
                }
            }
        }
        return maxEarliestFinish;
    }

    public static String formatDouble(Double num) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        if ((num % 1) == 0) {
            numberFormat.setMaximumFractionDigits(0);
            return numberFormat.format(num);
        } else {
            numberFormat.setMaximumFractionDigits(2);
            return numberFormat.format(num);
        }
    }

    public static String listToString(List<Activity> list) {
        String result = "";
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                result += list.get(i).getId();
                if (i != list.size() - 1) {
                    result += ", ";
                }
            }
        } else {
            result = "-";
        }
        return result;
    }

    public static List<Activity> stringToList(String str, List<Activity> data) {
        ArrayList<Activity> list = new ArrayList<>();

        if (str.trim().equals("-")) {
            return null;
        }
        String parts[] = str.replaceAll("[ \t]", "").split(",");

        for (String s : parts) {
            try {
                int id = Integer.parseInt(s);
                for (Activity a : data) {
                    if (a.getId() == id) {
                        list.add(a);
                        break;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return list;
    }

    public static void getAllSuccessors(Activity activity, ArrayList<Activity> result) {
        for (Activity a : activity.getSuccessors()) {
            result.add(a);
            getAllSuccessors(a, result);
        }
    }

    public static int getMaxNumOfPredecessors(Activity activity) {
        int max = 0;
        if (activity.getPredecessors().isEmpty()) {
            return 1;
        }
        for (Activity a : activity.getPredecessors()) {
            int local;
            local = 1 + getMaxNumOfPredecessors(a);
            if (local > max) max = local;
        }
        return max;
    }

    public static LinkedHashMap<Integer, Integer> calculateDepths(Map<Activity, DraggableActivityNode> nodes) {
        LinkedHashMap<Integer, Integer> depthsMap = new LinkedHashMap<>();
        for (Activity a : nodes.keySet()) {
            int depth = getMaxNumOfPredecessors(a);
            nodes.get(a).setGraphDepth(depth);
            depthsMap.put(depth, depthsMap.getOrDefault(depth, 0) + 1);
        }
        return depthsMap;
    }
}
