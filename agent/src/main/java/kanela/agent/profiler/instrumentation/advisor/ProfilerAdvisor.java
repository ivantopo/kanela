package kanela.agent.profiler.instrumentation.advisor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
    public static void onMethodExit(@Enter long start, @MethodInfo String methodDescription) {
        val timing = System.nanoTime() - start;
        System.out.println(String
            .format("|Profiling| Method %s was executed in %10.2f ns.", methodDescription, (float) timing));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface MethodInfo {
    }

}
