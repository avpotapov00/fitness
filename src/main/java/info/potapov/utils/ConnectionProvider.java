package info.potapov.utils;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionProvider {
    private static final String DB = "jdbc:postgresql://ec2-34-255-225-151.eu-west-1.compute.amazonaws.com:5432/dcda29fe02hjod";
    private static final String USER = "nxxnadiuqnyuvq";
    private static final String PASSWORD = "33f11e0090820c68cb23ecdc32376e21328bbba2efecc53e6de5111dfc04bd95";

    @SneakyThrows
    public static Connection connect() {
        var props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);

        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(DB, props);
    }
}
