package study.toby.chapter01.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * One operation represents one request using five immutable collaborators.
 * Context startup is deliberately outside the measured operation.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ObjectCreationBenchmark {
    private AnnotationConfigApplicationContext context;
    private Stage first;
    private Stage second;
    private Stage third;
    private Stage fourth;
    private Stage fifth;

    @Setup(Level.Trial)
    public void setUp() {
        context = new AnnotationConfigApplicationContext(SingletonStages.class);
        first = context.getBean("first", Stage.class);
        second = context.getBean("second", Stage.class);
        third = context.getBean("third", Stage.class);
        fourth = context.getBean("fourth", Stage.class);
        fifth = context.getBean("fifth", Stage.class);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        context.close();
    }

    @Benchmark
    public int createFivePerRequest(RequestSequence request, Blackhole blackhole) {
        return process(request.next(), new Stage(1), new Stage(2), new Stage(3),
                new Stage(4), new Stage(5), blackhole);
    }

    @Benchmark
    public int reuseFiveSingletonBeans(RequestSequence request, Blackhole blackhole) {
        return process(request.next(), first, second, third, fourth, fifth, blackhole);
    }

    private static int process(int value, Stage one, Stage two, Stage three,
                               Stage four, Stage five, Blackhole blackhole) {
        // Both paths perform the same work. Consuming the references stops the
        // JIT from discarding the newly created objects as unused temporaries.
        blackhole.consume(one);
        blackhole.consume(two);
        blackhole.consume(three);
        blackhole.consume(four);
        blackhole.consume(five);
        return five.apply(four.apply(three.apply(two.apply(one.apply(value)))));
    }

    @State(Scope.Thread)
    public static class RequestSequence {
        private int id;

        public int next() {
            return ++id;
        }
    }

    public record Stage(int salt) {
        public int apply(int input) {
            return input * 31 + salt;
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class SingletonStages {
        @Bean public Stage first()  { return new Stage(1); }
        @Bean public Stage second() { return new Stage(2); }
        @Bean public Stage third()  { return new Stage(3); }
        @Bean public Stage fourth() { return new Stage(4); }
        @Bean public Stage fifth()  { return new Stage(5); }
    }
}
