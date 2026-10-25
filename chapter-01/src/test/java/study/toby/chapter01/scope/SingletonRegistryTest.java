package study.toby.chapter01.scope;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import study.toby.chapter01.dao.DaoFactory;
import study.toby.chapter01.dao.PrimaryConnectionMaker;
import study.toby.chapter01.dao.UserDao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingletonRegistryTest {
    @Test
    void plainFactoryCreatesNewDaoForEveryCall() {
        DaoFactory factory = new DaoFactory(new PrimaryConnectionMaker());
        UserDao first = factory.userDao();
        UserDao second = factory.userDao();

        assertNotSame(first, second);
        assertFalse(first.equals(second)); // UserDao inherits Object.equals().
        assertTrue(first.equals(first));
    }

    @Test
    void equalsCanBeTrueEvenWhenReferencesDiffer() {
        String first = new String("same value");
        String second = new String("same value");

        assertNotSame(first, second);
        assertEquals(first, second); // String overrides equals().
    }

    @Test
    void springSharesOneBeanWithinEachContainer() {
        try (var firstContext = new AnnotationConfigApplicationContext(SpringDaoConfig.class);
             var secondContext = new AnnotationConfigApplicationContext(SpringDaoConfig.class)) {
            UserDao first = firstContext.getBean("userDao", UserDao.class);
            UserDao again = firstContext.getBean("userDao", UserDao.class);
            UserDao otherContainer = secondContext.getBean("userDao", UserDao.class);

            assertSame(first, again);
            assertNotSame(first, otherContainer);
            assertSame(first, firstContext.getBean(SpringDaoConfig.class).userDao());
        }
    }

    @Test
    void callingAnUnmanagedConfigurationObjectIsAnOrdinaryJavaCall() {
        SpringDaoConfig plainConfig = new SpringDaoConfig();
        assertNotSame(plainConfig.userDao(), plainConfig.userDao());
    }
}
