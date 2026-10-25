package study.toby.chapter01.scope;

import java.util.UUID;

public record StudyToken(UUID id) {
    public StudyToken() {
        this(UUID.randomUUID());
    }
}
