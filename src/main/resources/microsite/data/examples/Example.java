// tag:worker:start
import java.util.Random;

public class Worker {
  private final Random random = new Random();

  public void performTask() {
      Thread.sleep((long)(random.nextFloat() * 500));
  }
}
// tag:worker:end


// tag:mixin:start
public interface MonitorAware {
    Map<String, List<Long>> execTimings();
    List<Long> addExecTimings(String methodName, long time);
}
// tag:mixin:end


// tag:instrumentation:start
import kamon.agent.api.instrumentation.KamonInstrumentation;
// And other imports !
public class MonitorInstrumentation extends KamonInstrumentation {
    public MonitorInstrumentation() {
        forTargetType(() -> "app.kamon.java.Worker", builder ->
            builder.withMixin(() -> MonitorMixin.class)
                   .withAdvisorFor(named("performTask"), () -> WorkerAdvisor.class)
                   .build()
        );
    }
}
// tag:instrumentation:end


// tag:mixin-implementation:start
public class MonitorMixin implements MonitorAware {

    private Map<String, List<Long>> _execTimings;

    @Override
    public List<Long> execTimings(String methodName) {
        return _execTimings.getOrDefault(methodName, List.empty());
    }

    @Override
    public Map<String, List<Long>> execTimings() {
        return _execTimings;
    }

    @Override
    public List<Long> addExecTimings(String methodName, long time) {
        return this._execTimings.compute(methodName, (key, oldValues) -> Option.of(oldValues).map(vs -> vs.append(time)).getOrElse(List.of(time)));
    }

    @Initializer
    public void init() {
        this._execTimings = new ConcurrentHashMap<>();
    }
}
// tag:mixin-implementation:end

// tag:advisor:start
public class WorkerAdvisor {

    @OnMethodEnter
    public static long onMethodEnter() {
        return System.nanoTime(); // Return current time, entering as parameter in the onMethodExist
    }

    @OnMethodExit
    public static void onMethodExit(@This MonitorAware instance, @Enter long start, @Origin String origin) {
        long timing = System.nanoTime() - start;
        instance.addExecTimings(origin, timing);
        System.out.println(String.format("Method %s was executed in %10.2f ns.", origin, (float) timing));
    }
}
// tag:advisor:end
