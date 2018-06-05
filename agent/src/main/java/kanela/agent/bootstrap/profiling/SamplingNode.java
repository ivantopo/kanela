package kanela.agent.bootstrap.profiling;

import lombok.Value;

@Value(staticConstructor = "of")
public class SamplingNode {
    String methodSignature;
    long startTimeMethodNs;
    long endTimeMethodNs;
    long startTimeProfilingNs;
    long endTimeProfilingNs;
}
