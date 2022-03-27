package info.potapov.report;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

import info.potapov.report.query.QueryDaoImpl;
import com.mockrunner.mock.jdbc.MockResultSet;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;

public class QueryDaoImplTest {
    @Test
    public void testGetSubscriptionInfo() throws Exception {
        var connection = mock(Connection.class);

        var st = mock(PreparedStatement.class);
        var rs = new MockResultSet("rs");
        rs.addRow(new Object[]{"enter", 1L, Timestamp.valueOf("2020-09-23 10:10:00")});
        rs.addRow(new Object[]{"exit", 1L, Timestamp.valueOf("2020-09-23 12:10:00")});

        when(connection.prepareStatement(Mockito.startsWith("SELECT"))).thenReturn(st);

        when(st.executeQuery()).thenReturn(rs);

        var queryDao = new QueryDaoImpl(connection);
        var result = queryDao.getReport(LocalDate.parse("2020-09-23"), LocalDate.parse("2020-09-23"));

        assertEquals(result, "2020-09-23\n" + "  number of visits: 1\n" + "  average duration: 120\n");
    }

    @Test
    public void testGetSubscriptionInfo_LastTsUpdated() throws Exception {
        var connection = mock(Connection.class);

        var st = mock(PreparedStatement.class);
        var rs1 = new MockResultSet("rs1");
        rs1.addRow(new Object[]{"enter", 1L, Timestamp.valueOf("2020-09-23 10:10:00")});
        rs1.addRow(new Object[]{"exit", 1L, Timestamp.valueOf("2020-09-23 12:10:00")});
        rs1.addRow(new Object[]{"enter", 1L, Timestamp.valueOf("2020-09-23 11:10:00")});

        when(connection.prepareStatement(Mockito.startsWith("SELECT"))).thenReturn(st);

        var rs2 = new MockResultSet("rs2");
        rs2.addRow(new Object[]{"enter", 1L, Timestamp.valueOf("2020-09-23 11:10:00")});
        rs2.addRow(new Object[]{"exit", 1L, Timestamp.valueOf("2020-09-23 15:10:00")});

        when(st.executeQuery()).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                if (count++ == 1) {
                    return rs1;
                }

                return rs2;
            }
        });

        var queryDao = new QueryDaoImpl(connection);
        var result = queryDao.getReport(LocalDate.parse("2020-09-23"), LocalDate.parse("2020-09-23"));

        assertEquals(result, "2020-09-23\n" + "  number of visits: 2\n" + "  average duration: 180\n");
    }

    @Test
    public void testGetSubscriptionInfo_noReportDay() throws Exception {
        var connection = mock(Connection.class);

        var st = mock(PreparedStatement.class);
        var rs = new MockResultSet("rs");
        rs.addRow(new Object[]{"enter", 1L, Timestamp.valueOf("2020-09-23 10:10:00")});
        rs.addRow(new Object[]{"exit", 1L, Timestamp.valueOf("2020-09-23 12:10:00")});
        rs.addRow(new Object[]{"enter", 1L, Timestamp.valueOf("2020-09-25 10:10:00")});
        rs.addRow(new Object[]{"exit", 1L, Timestamp.valueOf("2020-09-25 12:10:00")});

        when(connection.prepareStatement(Mockito.startsWith("SELECT"))).thenReturn(st);

        when(st.executeQuery()).thenReturn(rs);

        var queryDao = new QueryDaoImpl(connection);
        var result = queryDao.getReport(LocalDate.parse("2020-09-23"), LocalDate.parse("2020-09-25"));

        assertEquals(result, "2020-09-23\n" + "  number of visits: 1\n" + "  average duration: 120\n"
                + "2020-09-25\n" + "  number of visits: 1\n" + "  average duration: 120\n");
    }
}
