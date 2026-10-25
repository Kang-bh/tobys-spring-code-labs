package study.toby.chapter01;

import java.util.Objects;

public final class DaoFactory {
    private final ConnectionMaker connectionMaker;

    public DaoFactory(ConnectionMaker connectionMaker) {
        this.connectionMaker = Objects.requireNonNull(connectionMaker);
    }

    public UserDao userDao() {
        return new UserDao(connectionMaker);
    }
}
