package study.toby.chapter01.scope;

// Deliberately unsafe: a singleton field is shared by simultaneous callers.
public final class UnsafeRequestState {
    private String currentUserId;

    public void begin(String userId) {
        currentUserId = userId;
    }

    public String currentUserId() {
        return currentUserId;
    }
}
