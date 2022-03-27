package info.potapov.manager.command.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import info.potapov.common.command.CommandDao;
import lombok.SneakyThrows;

public class CommandDaoImpl implements CommandDao {
    private final Connection conn;

    public CommandDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @SneakyThrows
    public String addNewUser() {
        var pstmt = conn.prepareStatement("INSERT INTO users DEFAULT VALUES",
                Statement.RETURN_GENERATED_KEYS);

        var affectedRows = pstmt.executeUpdate();

        long uid = -1;
        if (affectedRows > 0) {
            // get user id
            var rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                uid = rs.getLong(1);
            }

            if (uid != -1) {
                return "New uid is " + uid;
            }
        }

        // rollback the transaction if the insert failed
        conn.rollback();
        return "Error while inserting new user";
    }

    @SneakyThrows
    public String extendSubscription(Long uid, Date expiryDate) {
        var pstmt = conn.prepareStatement(
                "INSERT INTO membership_events(user_id, expiry_date) VALUES(?, ?) ");

        pstmt.setLong(1, uid);
        pstmt.setDate(2, expiryDate);

        var affectedRows = pstmt.executeUpdate();

        if (affectedRows > 0) {
            return "Subscription was successfully extended.";
        }

        // rollback the transaction if the insert failed
        conn.rollback();
        return "Error while extending subscription.";
    }
}
