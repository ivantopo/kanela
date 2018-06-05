package kanela.agent.bootstrap.profiling;

import java.util.Collections;
import java.util.Map;

public interface SamplingProvider {

    void add(String methodSignature, long startTimeNs, long endTimeNs);
    void clean();
    void start();
    Map<ThreadInfo, SamplingThread> stop();

    enum NoOp implements SamplingProvider {

        INSTANCE;

        public void add(String methodSignature, long startTimeNs, long endTimeNs) {}
        public void clean() {}
        public void start() {}
        public Map<ThreadInfo, SamplingThread> stop() {
            return Collections.emptyMap();
        }
    }

}
