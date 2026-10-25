package study.toby.chapter01.scope;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class ScopeConfig {
    @Bean
    @Scope("prototype")
    public StudyToken prototypeToken() {
        return new StudyToken();
    }

    @Bean
    public FixedTokenClient fixedTokenClient(@Qualifier("prototypeToken") StudyToken prototypeToken) {
        return new FixedTokenClient(prototypeToken);
    }

    @Bean
    public FreshTokenClient freshTokenClient(@Qualifier("prototypeToken") ObjectProvider<StudyToken> prototypeToken) {
        return new FreshTokenClient(prototypeToken);
    }

    @Bean
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public StudyToken requestToken() {
        return new StudyToken();
    }

    @Bean
    @Scope(WebApplicationContext.SCOPE_SESSION)
    public StudyToken sessionToken() {
        return new StudyToken();
    }
}
