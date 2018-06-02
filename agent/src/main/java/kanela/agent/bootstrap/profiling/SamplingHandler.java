package kanela.agent.bootstrap.profiling;

import kanela.agent.bootstrap.profiling.SamplingProvider.NoOp;

public final class SamplingHandler {

    private volatile static SamplingProvider samplingProvider = NoOp.INSTANCE;

    private SamplingHandler() {}

    public static void setSamplingProvider(SamplingProvider samplingProvider) {
        if (samplingProvider != NoOp.INSTANCE) {
            SamplingHandler.samplingProvider = samplingProvider;
        }
    }

    public static void add(String methodSignature, Long timing) {
        samplingProvider.add(methodSignature, timing);
    }

    public static void clean() {
        samplingProvider.clean();
    }

    public static void start() {
        samplingProvider.start();
    }

    public static void stop() {
        samplingProvider.stop();
    }

}
