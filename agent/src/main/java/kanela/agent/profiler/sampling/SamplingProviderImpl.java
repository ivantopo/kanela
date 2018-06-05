package kanela.agent.profiler.sampling;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import kanela.agent.bootstrap.profiling.SamplingNode;
import kanela.agent.bootstrap.profiling.SamplingProvider;
import kanela.agent.bootstrap.profiling.SamplingThread;
import kanela.agent.bootstrap.profiling.ThreadInfo;
import kanela.agent.util.log.Logger;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.val;

@Value
public class SamplingProviderImpl implements SamplingProvider {

    public static Random random = new Random();

    private static class Holder {
        private static final SamplingProvider Instance = new SamplingProviderImpl();
    }

    public static SamplingProvider instance() {
        return Holder.Instance;
    }

    private static Map<ThreadInfo, SamplingThreadImpl> accumulators = new ConcurrentHashMap<>();
    @NonFinal private static volatile boolean active = false;

    private static ThreadLocal<ThreadInfo> samplingThread = ThreadLocal.withInitial(() -> {
        val thread = Thread.currentThread();
        return ThreadInfo.newOne(thread.getName(), thread.getId());
    });

    public void add(String methodSignature, long startTimeNs, long endTimeNs) {
        if (!active) return;
        if (random.nextFloat() < 0.05)
            Logger.debug(() -> format("Sampling method %s which was executed in %10.2f ns.", methodSignature, (float) (endTimeNs - startTimeNs)));
        long startTimeProfilingNs = System.nanoTime();
        val sampling = samplingThread.get();
        accumulators
            .computeIfAbsent(sampling, (k) -> SamplingThreadImpl.newOne())
            .add(() -> SamplingNode.of(methodSignature, startTimeNs, endTimeNs, startTimeProfilingNs, System.nanoTime()));
    }

    public void clean() {
        accumulators.clear();
    }

    public void start() {
        active = true;
        System.out.println("Starting to recording samples");
        Logger.debug(() -> "Starting to recording samples");
    }

    public Map<ThreadInfo, SamplingThread> stop() {
        active = false;
        System.out.println("Sampling is stopping. Recorded values:");
        Logger.debug(() -> "Sampling is stopping. Recorded values:");
        accumulators.forEach((key, value) -> {
            System.out.println(format("Key: %s, Value: %s", key, value));
            Logger.debug(() -> format("Key: %s, Value: %s", key, value));
        });
        return accumulators.entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    /**
     * Accumulator used per single thread, therefore it's not thread safe.
     */
    @Value(staticConstructor = "newOne")
    static final class SamplingThreadImpl implements SamplingThread {
        List<SamplingNode> samplings = new ArrayList<>();

        public void add(Supplier<SamplingNode> sample) {
            samplings.add(sample.get());
        }

//        public List<SamplingNode> samplingNodesByMethodSignature(String methodSignature) {
//            return samplings.getOrDefault(methodSignature, Collections.emptyList());
//        }
    }
}
