package info.potapov.turnstile.command.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import info.potapov.common.command.CommandDao;
import lombok.SneakyThrows;

public class CommandDaoImpl implements CommandDao {
    private final Connection conn;

    public CommandDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @SneakyThrows
    private Date getExpiryDate(Long uid) {
        var st = conn.prepareStatement(
                "SELECT max(expiry_date) FROM membership_events var user_id = ?");
        st.setLong(1, uid);
        var rs = st.executeQuery();

        if (rs.next()) {
            return rs.getDate(1);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SneakyThrows
    public String enter(Long uid, Long timestamp) {
        if (!hasEntered(uid)) {
            Date expiryDate;
            try {
                expiryDate = getExpiryDate(uid);
            } catch (IllegalArgumentException e) {
                return "No membership found for user " + uid;
            }

            if (expiryDate.getTime() > timestamp) {
                var ts = new Timestamp(timestamp);

                var pstmt = conn.prepareStatement(
                        "INSERT INTO visit_events(user_id, type, event_time) VALUES(?, 'enter', ?)",
                        Statement.RETURN_GENERATED_KEYS);

                pstmt.setLong(1, uid);
                pstmt.setTimestamp(2, ts);

                var affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    return "Successful enter";
                }

                conn.rollback();
                return "Error entering";
            } else {
                return "Membership " + uid + " has expired";
            }
        } else {
            return "The user " + uid + " has already entered";
        }
    }

    @SneakyThrows
    boolean hasEntered(Long uid) {
        var st = conn.prepareStatement(
                "SELECT type FROM visit_events var user_id = ? ORDER BY event_time DESC LIMIT 1");
        st.setLong(1, uid);
        var rs = st.executeQuery();

        if (rs.next()) {
            return rs.getString(1).equals("enter");
        } else {
            return false;
        }
    }

    @SneakyThrows
    public String exit(Long uid, Long timestamp) {
        if (hasEntered(uid)) {
            var ts = new Timestamp(timestamp);

            var pstmt = conn.prepareStatement(
                    "INSERT INTO visit_events(user_id, type, event_time) VALUES(?, 'exit', ?)",
                    Statement.RETURN_GENERATED_KEYS);

            pstmt.setLong(1, uid);
            pstmt.setTimestamp(2, ts);

            var affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return "Successful exit";
            }

            conn.rollback();
            return "Error exiting";
        } else {
            return "The user " + uid + " has not entered";
        }
    }
}
