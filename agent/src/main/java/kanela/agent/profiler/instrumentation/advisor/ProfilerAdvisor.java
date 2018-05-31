package kanela.agent.profiler.instrumentation.advisor;

import lombok.val;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;

public class ProfilerAdvisor {

  @OnMethodEnter
  public static long onMethodEnter() {
    return System.nanoTime(); // Return current time, entering as parameter in the onMethodExist
  }

  @OnMethodExit
  public static void onMethodExit(@Enter long start, @Origin String origin) {
    val timing = System.nanoTime() - start;
    System.out.println(String.format("|Profiling| Method %s was executed in %10.2f ns.", origin, (float) timing));
  }

}
