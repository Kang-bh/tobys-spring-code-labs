package study.toby.scope;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateSafetyTest {
    @Test
    void aSharedMutableFieldLetsOneRequestOverwriteAnother() throws Exception {
        UnsafeRequestState shared = new UnsafeRequestState();
        CountDownLatch aliceStarted = new CountDownLatch(1);
        CountDownLatch bobOverwrote = new CountDownLatch(1);

        var workers = Executors.newFixedThreadPool(2);
        try {
            var alice = workers.submit(() -> {
                shared.begin("alice");
                aliceStarted.countDown();
                if (!bobOverwrote.await(5, TimeUnit.SECONDS)) throw new AssertionError("bob did not run");
                return shared.currentUserId();
            });
            var bob = workers.submit(() -> {
                if (!aliceStarted.await(5, TimeUnit.SECONDS)) throw new AssertionError("alice did not run");
                shared.begin("bob");
                bobOverwrote.countDown();
                return shared.currentUserId();
            });

            assertEquals("bob", alice.get(5, TimeUnit.SECONDS));
            assertEquals("bob", bob.get(5, TimeUnit.SECONDS));
        } finally {
            workers.shutdownNow();
        }
    }

    @Test
    void localInputStaysWithItsOwnCall() {
        StatelessUserService service = new StatelessUserService();
        assertEquals("user=alice", service.describe("alice"));
        assertEquals("user=bob", service.describe("bob"));
    }
}
