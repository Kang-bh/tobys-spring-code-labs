package study.toby.chapter01.scope;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.toby.chapter01.dao.ConnectionMaker;
import study.toby.chapter01.dao.PrimaryConnectionMaker;
import study.toby.chapter01.dao.UserDao;

@Configuration
public class SpringDaoConfig {
    @Bean
    public ConnectionMaker connectionMaker() {
        return new PrimaryConnectionMaker();
    }

    @Bean
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }
}
