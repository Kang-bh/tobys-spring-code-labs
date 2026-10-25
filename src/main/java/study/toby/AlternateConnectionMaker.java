package study.toby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class AlternateConnectionMaker implements ConnectionMaker {
    @Override
    public Connection makeConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:alternate;DB_CLOSE_DELAY=-1");
    }
}
