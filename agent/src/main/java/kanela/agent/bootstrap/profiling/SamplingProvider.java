package kanela.agent.bootstrap.profiling;

public interface SamplingProvider {

    void add(String methodSignature, Long time);
    void clean();
    void start();
    void stop();

    enum NoOp implements SamplingProvider {

        INSTANCE;

        public void add(String methodSignature, Long time) {}
        public void clean() {}
        public void start() {}
        public void stop() {}
    }

}
