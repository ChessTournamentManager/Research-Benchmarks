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

@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ResearchTest {

    public static ResearchRepository researchRepository;
    @Mock
    private ResearchRepository mockedResearchRepository;
    private AutoCloseable autoCloseable;
    private UUID readResearchId;

    @Autowired
    public void setResearchRepository(ResearchRepository researchRepository) {
        ResearchTest.researchRepository = researchRepository;
    }

    @AfterEach
    void tearDown() throws Exception {
        researchRepository.deleteAll();
    }


    @Test
    public void runBenchmarks() throws Exception {
        Options opts = new OptionsBuilder()
                // set the class name regex for benchmarks to search for to the current class
                .include("\\." + this.getClass().getSimpleName() + "\\.")
                // do not use forking or the benchmark methods will not see references stored within its class
                .forks(0)
                // do not use multiple threads
                .threads(1)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .jvmArgs("-server")
                .build();

        new Runner(opts).run();
    }

    @Setup(Level.Trial)
    public void initBenchmark() {
        Research research = new Research("Very Cool Research", 20000);
        readResearchId = research.getId();
        researchRepository.save(research);
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @TearDown(Level.Trial)
    public void endBenchmark() throws Exception {
        autoCloseable.close();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    public void BM1_CreateObject(Blackhole blackhole) {
        blackhole.consume(new Research("Cool Research", 5000));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    public void BM2_MockWriteObject() {
        mockedResearchRepository.save(new Research("Cool Research", 5000));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void BM3_WriteObject() {
        researchRepository.save(new Research("Cool Research", 5000));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 10, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void BM4_RetrieveKey(Blackhole blackhole) {
        blackhole.consume(readResearchId);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
    public void BM5_MockReadObject(Blackhole blackhole) {
        blackhole.consume(mockedResearchRepository.findById(readResearchId));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 3, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void BM6_ReadObject(Blackhole blackhole) {
        blackhole.consume(researchRepository.findById(readResearchId));
    }
}