package study.toby.chapter01.scope;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.toby.chapter01.ConnectionMaker;
import study.toby.chapter01.PrimaryConnectionMaker;
import study.toby.chapter01.UserDao;

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
