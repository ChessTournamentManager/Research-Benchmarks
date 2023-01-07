package com.chesstournamentmanager.springjmhredis.BenchmarkTests;

import com.chesstournamentmanager.springjmhredis.models.Research;
import com.chesstournamentmanager.springjmhredis.repositories.ResearchRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

// The @SpringBootTest annotation allows other classes to be autowired into this class.
// The @State annotation is required for a class that has fields that are being used for benchmarks.
// The @BenchmarkMode annotation allows us to customize in which format we want our benchmark results.
// In this case, the benchmark results will show the average completion time for a single operation.
// The @OutputTimeUnit allows us to choose a time unit in which our tests are measured.
// In this case, the tests are measured in microseconds.
@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ResearchTest {

    // The real repository for our tests. This needs to be static, otherwise the autowiring won't work with JMH.
    public static ResearchRepository researchRepository;

    // Some fields that are needed for mock tests.
    @Mock
    private ResearchRepository mockedResearchRepository;
    private AutoCloseable autoCloseable;

    // The ID of the Research object that needs to be read from the database.
    private UUID readResearchId;

    // Auto-wires the repository from a set method. Normally this would be done in the constructor of this class, but
    // JMH needs this class to have the default constructor.
    @Autowired
    public void setResearchRepository(ResearchRepository researchRepository) {
        ResearchTest.researchRepository = researchRepository;
    }

    // Deletes all records in the database after each JUnit5 test.
    @AfterEach
    void tearDown() throws Exception {
        researchRepository.deleteAll();
    }

    // In this test, the benchmark options are set.
    // Even though multiple benchmark tests are being run, this is the only JUnit test in the class.
    // All benchmarks are run in this test.
    @Test
    public void runBenchmarks() throws Exception {
        Options opts = new OptionsBuilder()
                // Set the class name regex for benchmarks to search for to the current class.
                .include("\\." + this.getClass().getSimpleName() + "\\.")
                // Do not use forking or the benchmark methods will not see references stored within its class.
                .forks(0)
                // Do not use multiple threads.
                .threads(1)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .jvmArgs("-server")
                .build();

        new Runner(opts).run();
    }

    // Saves a research object in the database and stores its ID in the 'readResearchId' field.
    // This is useful for the read test. The autocloseable enables mocking within our benchmark tests.
    @Setup(Level.Trial)
    public void initBenchmark() {
        Research research = new Research("Very Cool Research", 20000);
        readResearchId = research.getId();
        researchRepository.save(research);
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    // Cleans up resources by closing the autocloseable after the completion of each benchmark.
    @TearDown(Level.Trial)
    public void endBenchmark() throws Exception {
        autoCloseable.close();
    }

    // The first benchmark test. In this benchmark, a Research object is created.
    // After that, the black hole object will consume the object.
    // This prevents the compiler from optimizing the object creation code after it has already executed it.
    // The @Benchmark annotation registers this method as a benchmark test.
    // The @Warmup annotation allows us to customize how many warmup runs are done before the real benchmarking begins.
    // In this case, 3 warmup iterations are done. Each iteration takes 10 milliseconds to complete.
    // The @Measurement annotation allows us to customize how many runs are done during the benchmark.
    // In this case, 20 iterations are done. Each iteration takes 10 milliseconds to complete.
    @Benchmark
    @Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    public void BM1_CreateObject(Blackhole blackhole) {
        blackhole.consume(new Research("Cool Research", 5000));
    }

    // The second benchmark test.
    // In this benchmark, a Research object is created and the 'save' method of a mocked repository is being called.
    // Each iteration now takes 200 milliseconds.
    @Benchmark
    @Warmup(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    public void BM2_MockWriteObject() {
        mockedResearchRepository.save(new Research("Cool Research", 5000));
    }

    // The third benchmark test.
    // In this benchmark, a Research object is created, the 'save' method of the repository is being called.
    // Then, the database executes a write operation and saves the Research object.
    // Each iteration now takes a second to complete.
    @Benchmark
    @Warmup(iterations = 3, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void BM3_WriteObject() {
        researchRepository.save(new Research("Cool Research", 5000));
    }

    // The fourth benchmark test.
    // In this benchmark, the value of the 'readResearchId' variable is being retrieved.
    // The results of this benchmark are measured in nanoseconds instead of microseconds.
    // Nanoseconds are chosen here, because each operation happens very quickly.
    // Choosing a smaller time unit increases the accuracy of the results.
    @Benchmark
    @Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void BM4_RetrieveKey(Blackhole blackhole) {
        blackhole.consume(readResearchId);
    }

    // The fifth benchmark test.
    // In this benchmark, the value of the 'readResearchId' variable is being retrieved.
    // Then, the 'findById' method of a mocked repository is being called.
    @Benchmark
    @Warmup(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    public void BM5_MockReadObject(Blackhole blackhole) {
        blackhole.consume(mockedResearchRepository.findById(readResearchId));
    }

    // The sixth benchmark test.
    // In this benchmark, the value of the 'readResearchId' variable is being retrieved.
    // Then, the 'findById' method of the repository is being called.
    // Then, the database executes a read operation with the passed ID and reads the correct Research object.
    @Benchmark
    @Warmup(iterations = 3, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void BM6_ReadObject(Blackhole blackhole) {
        blackhole.consume(researchRepository.findById(readResearchId));
    }
}