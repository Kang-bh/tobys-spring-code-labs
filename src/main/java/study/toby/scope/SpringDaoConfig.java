package study.toby.scope;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.toby.ConnectionMaker;
import study.toby.PrimaryConnectionMaker;
import study.toby.UserDao;

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
