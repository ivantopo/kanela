package kanela.agent.profiler.instrumentation.advisor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kanela.agent.bootstrap.profiling.SamplingHandler;
import kanela.agent.bootstrap.utils.KanelaRandom;
import lombok.val;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;

public class ProfilerAdvisor {

    @OnMethodEnter
    public static long onMethodEnter() {
        return System.nanoTime(); // Return current time, entering as parameter in the onMethodExist
    }

    @OnMethodExit
    public static void onMethodExit(@Enter long startTimeNs, @MethodInfo String methodDescription) {
        val endTimeNs = System.nanoTime();
        SamplingHandler.add(methodDescription, startTimeNs, endTimeNs);
        if (KanelaRandom.random.nextFloat() < 0.05)
            System.out.println(String
                .format("|Profiling| Method %s was executed in %10.2f ns.", methodDescription, (float) (endTimeNs - startTimeNs)));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface MethodInfo {
    }

}
