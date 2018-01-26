// tag:worker:start
import java.util.Random;

public class Worker {
  private final Random random = new Random();

  public void performTask() {
      Thread.sleep((long)(random.nextFloat() * 500));
  }
}
// tag:worker:end

// tag:instrumentation:start

public class TimeSpentInstrumentation extends KamonInstrumentation {
    public MonitorInstrumentation() {
        forTargetType(() -> "run.Worker", builder ->
            builder.withAdvisorFor(method("performTask"), () -> PerformTaskMethodAdvisor.class)
                   .build()
        );
    }
}
// tag:instrumentation:end

// tag:advisor:start
public class PerformTaskMethodAdvisor {

    @OnMethodEnter
    public static long onMethodEnter() {
        return System.nanoTime(); // Return current time, entering as parameter in the onMethodExist
    }

    @OnMethodExit
    public static void onMethodExit(@This MonitorAware instance, @Enter long start, @Origin String origin) {
        System.out.println(String.format("Method %s was executed in %10.2f ns.", origin, (float) System.nanoTime() - start));
    }
}
// tag:advisor:end



// tag:run:start
public class TimeSpentJava {
    public static void main(String... args) {
        for(int i = 0; i < 10; i++) {
            new Worker().performTask();
        }
    }
}
// tag:run:end
