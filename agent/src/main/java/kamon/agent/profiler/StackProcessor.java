package kamon.agent.profiler;

import java.util.function.Function;

public interface StackProcessor {
    void sample(Thread ignore);

    void addSample(StackTraceElement[] stackTrace);

    SampleNode applyOnSamples(Function<SampleNode, SampleNode> transform);

    SampleNode clear();
}
