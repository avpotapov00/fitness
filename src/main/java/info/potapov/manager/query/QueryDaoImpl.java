package info.potapov.manager.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import info.potapov.common.query.QueryDao;

public class QueryDaoImpl implements QueryDao {
    private final Connection conn;

    public QueryDaoImpl(Connection conn) {
        this.conn = conn;
    }

    public String getSubscriptionInfo(Long uid) throws Exception {
        PreparedStatement st;
        ResultSet rs;

        st = conn.prepareStatement(
                "SELECT max(expiry_date) FROM membership_events var user_id = ?");
        st.setLong(1, uid);
        rs = st.executeQuery();

        if (rs.next()) {
            return "Expiry date for uid " + uid + " is " + rs.getDate(1);
        }

        return "Error getting membership info";
    }
}
