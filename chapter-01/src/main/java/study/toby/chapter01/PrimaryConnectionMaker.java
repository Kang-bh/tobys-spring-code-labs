package study.toby.chapter01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class PrimaryConnectionMaker implements ConnectionMaker {
    @Override
    public Connection makeConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:primary;DB_CLOSE_DELAY=-1");
    }
}
