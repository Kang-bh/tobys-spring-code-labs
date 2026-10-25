package study.toby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class UserDao {
    private final ConnectionMaker connectionMaker;

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = java.util.Objects.requireNonNull(connectionMaker);
    }

    public void add(User user) throws SQLException {
        try (Connection connection = connectionMaker.makeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users (id, name, password) VALUES (?, ?, ?)")) {
            statement.setString(1, user.id());
            statement.setString(2, user.name());
            statement.setString(3, user.password());
            statement.executeUpdate();
        }
    }

    public Optional<User> get(String id) throws SQLException {
        try (Connection connection = connectionMaker.makeConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, password FROM users WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return Optional.empty();
                return Optional.of(new User(result.getString("id"), result.getString("name"),
                        result.getString("password")));
            }
        }
    }
}
