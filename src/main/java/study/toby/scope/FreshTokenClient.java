package study.toby.scope;

import org.springframework.beans.factory.ObjectProvider;

public record FreshTokenClient(ObjectProvider<StudyToken> provider) {
    public StudyToken nextToken() {
        return provider.getObject();
    }
}
