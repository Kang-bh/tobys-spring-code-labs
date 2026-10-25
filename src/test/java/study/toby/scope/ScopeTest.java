package study.toby.scope;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class ScopeTest {
    @Test
    void prototypeCreatesNewObjectsButDirectInjectionHappensOnlyOnce() {
        try (var context = new AnnotationConfigApplicationContext(ScopeConfig.class)) {
            assertNotSame(context.getBean("prototypeToken"), context.getBean("prototypeToken"));

            FixedTokenClient fixed = context.getBean(FixedTokenClient.class);
            assertSame(fixed.token(), fixed.token());

            FreshTokenClient fresh = context.getBean(FreshTokenClient.class);
            assertNotSame(fresh.nextToken(), fresh.nextToken());
        }
    }

    @Test
    void webScopesFollowRequestAndSessionLifetimes() {
        try (var context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(ScopeConfig.class);
            context.refresh();

            MockHttpSession sharedSession = new MockHttpSession();
            MockHttpServletRequest firstRequest = request(sharedSession);
            MockHttpServletRequest secondRequest = request(sharedSession);
            MockHttpServletRequest otherSessionRequest = request(new MockHttpSession());
            try {
                bind(firstRequest);
                StudyToken firstRequestToken = context.getBean("requestToken", StudyToken.class);
                StudyToken firstSessionToken = context.getBean("sessionToken", StudyToken.class);
                assertSame(firstRequestToken, context.getBean("requestToken"));
                assertSame(firstSessionToken, context.getBean("sessionToken"));

                bind(secondRequest);
                assertNotSame(firstRequestToken, context.getBean("requestToken"));
                assertSame(firstSessionToken, context.getBean("sessionToken"));

                bind(otherSessionRequest);
                assertNotSame(firstSessionToken, context.getBean("sessionToken"));
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }

    private static MockHttpServletRequest request(MockHttpSession session) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        return request;
    }

    private static void bind(MockHttpServletRequest request) {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
