package kanela.agent.profiler;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import kanela.agent.bootstrap.profiling.SamplingProvider;
import kanela.agent.util.log.Logger;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.val;

@Value
public class SamplingProviderImpl implements SamplingProvider {

    private static int MAX_DEPTH = 300;

    private static class Holder {
        private static final SamplingProvider Instance = new SamplingProviderImpl();
    }

    public static SamplingProvider instance() {
        return Holder.Instance;
    }

    private static Map<Key, Accumulator> accumulators = new ConcurrentHashMap<>();
    private static ThreadLocal<Key> keyTL = ThreadLocal.withInitial(Key::new);
    @NonFinal private static volatile boolean active = false;

    public void add(String methodSignature, Long time) {
        if (!active) return;
        val key = keyTL.get();
        accumulators
            .computeIfAbsent(key, (k) -> Accumulator.newOne())
            .add(() -> Sample.of(methodSignature, time));
    }

    public void clean() {
        accumulators.clear();
    }

    public void start() {
        active = true;
        System.out.println("Starting to recording samples");
        Logger.debug(() -> "Starting to recording samples");
    }

    public void stop() {
        active = false;
        System.out.println("Finishing of recording samples");
        Logger.debug(() -> "Finishing of recording samples");
        Map<String, String> result = new HashMap<>();
        val iterator = accumulators.entrySet().iterator();
        System.out.println("Samples values:");
        Logger.debug(() -> "Samples values:");
        while (iterator.hasNext()) {
            final Entry<Key, Accumulator> next = iterator.next();
            System.out.println(format("Key: %s, Value: %s", next.getKey(), next.getValue()));
            Logger.debug(() -> format("Key: %s, Value: %s", next.getKey(), next.getValue()));
        }
        clean();
    }

    public static class Key {
        Key() {}
    }

    /**
     * Accumulator used per single thread, therefore it's not thread safe.
     */
    @Value(staticConstructor = "newOne")
    private static class Accumulator {
        Map<String, List<Sample>> samplesByKey = new HashMap<>(MAX_DEPTH);

        public void add(Supplier<Sample> sample) {
            if (samplesByKey.size() < MAX_DEPTH) {
                final Sample s = sample.get();
                samplesByKey
                    .computeIfAbsent(s.methodSignature, (k) -> new ArrayList<>())
                    .add(sample.get());
            }
        }
    }

    @Value(staticConstructor = "of")
    private static class Sample {
        String methodSignature;
        Long time;
    }

}
