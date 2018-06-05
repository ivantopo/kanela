package kanela.agent.bootstrap.profiling;

import java.util.Map;
import kanela.agent.bootstrap.profiling.SamplingProvider.NoOp;

public final class SamplingHandler {

    private static SamplingProvider samplingProvider = NoOp.INSTANCE;

    private SamplingHandler() {}

    public static void setSamplingProvider(SamplingProvider samplingProvider) {
        if (samplingProvider != NoOp.INSTANCE) {
            SamplingHandler.samplingProvider = samplingProvider;
        }
    }

    public static void add(String methodSignature, long startTimeNs, long endTimeNs) {
        samplingProvider.add(methodSignature, startTimeNs, endTimeNs);
    }

    public static void clean() {
        samplingProvider.clean();
    }

    public static void start() {
        samplingProvider.start();
    }

    public static Map<ThreadInfo, SamplingThread> stop() {
        return samplingProvider.stop();
    }

}
