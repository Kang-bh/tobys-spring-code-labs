package study.toby.chapter01.dao;

import java.sql.Connection;
import java.sql.Statement;

public final class Demo {
    private Demo() {}

    public static void main(String[] args) throws Exception {
        run("primary", new PrimaryConnectionMaker());
        run("alternate", new AlternateConnectionMaker());
    }

    private static void run(String label, ConnectionMaker maker) throws Exception {
        try (Connection connection = maker.makeConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id VARCHAR(100) PRIMARY KEY, name VARCHAR(100), password VARCHAR(100))");
            statement.executeUpdate("DELETE FROM users");
        }
        UserDao dao = new DaoFactory(maker).userDao();
        dao.add(new User("u1", label, "example-only"));
        System.out.println(label + ": " + dao.get("u1").orElseThrow());
    }
}
