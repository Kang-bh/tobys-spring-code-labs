package study.toby.scope;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import study.toby.DaoFactory;
import study.toby.PrimaryConnectionMaker;

public final class ScopeDemo {
    private ScopeDemo() { }

    public static void main(String[] args) {
        DaoFactory plainFactory = new DaoFactory(new PrimaryConnectionMaker());
        System.out.println("plain DaoFactory: " + (plainFactory.userDao() == plainFactory.userDao()));

        try (var context = new AnnotationConfigApplicationContext(SpringDaoConfig.class, ScopeConfig.class)) {
            System.out.println("Spring singleton: " + (context.getBean("userDao") == context.getBean("userDao")));
            System.out.println("prototype: " + (context.getBean("prototypeToken") == context.getBean("prototypeToken")));
            FreshTokenClient client = context.getBean(FreshTokenClient.class);
            System.out.println("provider lookup: " + (client.nextToken() == client.nextToken()));
        }
    }
}
