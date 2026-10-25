package study.toby.chapter01;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DaoFactoryTest {
    @Test
    void factoryInjectsConnectionMakerAndDaoCanReadWhatItWrites() throws Exception {
        ConnectionMaker maker = new PrimaryConnectionMaker();
        reset(maker);
        UserDao dao = new DaoFactory(maker).userDao();
        User user = new User("a", "Alice", "sample");

        dao.add(user);

        assertEquals(user, dao.get("a").orElseThrow());
        assertTrue(dao.get("missing").isEmpty());
    }

    @Test
    void switchingMakerInCompositionDoesNotRequireChangingUserDao() throws Exception {
        ConnectionMaker primary = new PrimaryConnectionMaker();
        ConnectionMaker alternate = new AlternateConnectionMaker();
        reset(primary);
        reset(alternate);
        UserDao primaryDao = new DaoFactory(primary).userDao();
        UserDao alternateDao = new DaoFactory(alternate).userDao();

        primaryDao.add(new User("same-id", "Primary", "sample"));
        alternateDao.add(new User("same-id", "Alternate", "sample"));

        assertEquals("Primary", primaryDao.get("same-id").orElseThrow().name());
        assertEquals("Alternate", alternateDao.get("same-id").orElseThrow().name());
    }

    private static void reset(ConnectionMaker maker) throws Exception {
        try (Connection connection = maker.makeConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id VARCHAR(100) PRIMARY KEY, name VARCHAR(100), password VARCHAR(100))");
            statement.executeUpdate("DELETE FROM users");
        }
    }
}
