package info.potapov.report.query;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Report {
    private final Map<LocalDate, DayReport> days;
    long lastTsReport;

    public Report() {
        lastTsReport = 0;
        days = new HashMap<>();
    }

    public void setLastTsReport(long lastTsReport) {
        this.lastTsReport = lastTsReport;
    }

    public long getLastTsReport() {
        return lastTsReport;
    }

    public void addVisit(LocalDate date, long minutes) {
        if (!days.containsKey(date)) {
            days.put(date, new DayReport());
        }
        var dayReport = days.get(date);
        var times = dayReport.getTimes();
        dayReport.setAverageDuration(
                (times * dayReport.getAverageDuration() + minutes) / (times + 1));
        dayReport.setTimes(times + 1);
    }

    public String toString(LocalDate from, LocalDate to) {
        var date = from;
        var until = to.plusDays(1);
        var sb = new StringBuilder();

        while (!date.equals(until)) {
            var dayReport = days.getOrDefault(date, null);
            if (dayReport != null) {
                sb.append(date.toString());
                sb.append("\n");

                sb.append("  number of visits: ");
                sb.append(dayReport.getTimes());
                sb.append("\n");

                sb.append("  average duration: ");
                sb.append(dayReport.getAverageDuration());
                sb.append("\n");
            }
            date = date.plusDays(1);
        }
        return sb.toString();
    }

    public static class DayReport {
        long times = 0L;
        long averageDuration = 0L;

        public void setAverageDuration(Long averageDuration) {
            this.averageDuration = averageDuration;
        }

        public void setTimes(Long times) {
            this.times = times;
        }

        public Long getAverageDuration() {
            return averageDuration;
        }

        public Long getTimes() {
            return times;
        }
    }
}
