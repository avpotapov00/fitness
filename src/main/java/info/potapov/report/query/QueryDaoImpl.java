package info.potapov.report.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import info.potapov.common.query.QueryDao;

public class QueryDaoImpl implements QueryDao {
    private final Connection conn;
    private final Report report;

    public QueryDaoImpl(Connection conn) throws Exception {
        this.conn = conn;
        this.report = new Report();
        updateReport();
    }

    public String getReport(LocalDate from, LocalDate to) throws Exception {
        updateReport();
        return report.toString(from, to);
    }

    void updateReport() throws Exception {
        var st = conn.prepareStatement(
                "SELECT type, user_id, event_time FROM visit_events WHERE event_time >= ?");
        st.setTimestamp(1, new Timestamp(report.getLastTsReport()));
        var rs = st.executeQuery();

        List<Record> records = getAllRecords(rs);

        SortedSet<Long> enters = new TreeSet<>();
        Map<Long, Long> id_to_ts = new HashMap<>();
        long last_ts;
        for (Record record : records) {
            var userId = record.getUserId();
            var ts = record.getEventTime();

            if (record.getType().equals("enter")) {
                id_to_ts.put(userId, ts.getTime());
                enters.add(ts.getTime());
            } else {
                var enter_ts = id_to_ts.getOrDefault(userId, null);
                if (enter_ts != null) {
                    var enter = new Timestamp(enter_ts).toLocalDateTime();
                    var exit = new Timestamp(ts.getTime()).toLocalDateTime();
                    // count only one-day visits - those are true client visits, not technical ones.
                    if (enter.toLocalDate().equals(exit.toLocalDate())) {
                        report.addVisit(enter.toLocalDate(), enter.until(exit, ChronoUnit.MINUTES));
                    }
                    enters.remove(enter_ts);
                }
                id_to_ts.remove(userId);
            }
            last_ts = ts.getTime();
            if (!enters.isEmpty()) {
                last_ts = enters.first();
            } else {
                last_ts = last_ts + 1;
            }
            report.setLastTsReport(last_ts);
        }
    }

    public static List<Record> getAllRecords(ResultSet rs) throws Exception {
        List<Record> records = new ArrayList<>();

        while (rs.next()) {
            var type = rs.getString(1);
            var userId = rs.getLong(2);
            var ts = rs.getTimestamp(3);

            records.add(new Record(userId, ts, type));
        }

        return records;
    }

    public static class Record {
        Long userId;
        Timestamp eventTime;
        String type;

        public Record(Long userId, Timestamp eventTime, String type) {
            this.userId = userId;
            this.eventTime = eventTime;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public Long getUserId() {
            return userId;
        }

        public Timestamp getEventTime() {
            return eventTime;
        }
    }
}
