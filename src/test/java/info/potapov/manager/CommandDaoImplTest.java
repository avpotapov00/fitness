package info.potapov.manager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import info.potapov.manager.command.dao.CommandDaoImpl;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

public class CommandDaoImplTest {
    @Test
    public void testAddNewUser() throws Exception {
        var connection = mock(Connection.class);
        var st = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);

        when(connection.prepareStatement(Mockito.startsWith("INSERT"), Mockito.anyInt())).thenReturn(st);

        when(st.executeUpdate()).thenReturn(1);
        when(st.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getLong(1)).thenReturn(5L);

        var commandDao = new CommandDaoImpl(connection);
        var result = commandDao.addNewUser();

        assertEquals(result, "New uid is 5");
    }

    @Test
    public void testExtendSubscription() throws Exception {
        var connection = mock(Connection.class);
        var st = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);

        when(connection.prepareStatement(Mockito.startsWith("INSERT"))).thenReturn(st);
        when(st.executeUpdate()).thenReturn(1);

        var commandDao = new CommandDaoImpl(connection);
        var expiryDate = Date.valueOf("2020-02-02");

        var result = commandDao.extendSubscription(2L, expiryDate);

        assertEquals(result, "Subscription was successfully extended.");

        verify(st, times(1)).setLong(1, 2L);
        verify(st, times(1)).setDate(2, expiryDate);
    }
}
