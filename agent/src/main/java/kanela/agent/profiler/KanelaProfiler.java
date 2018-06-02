package kanela.agent.profiler;

import io.vavr.control.Option;
import java.lang.instrument.Instrumentation;
import kanela.agent.bootstrap.profiling.SamplingHandler;
import kanela.agent.profiler.instrumentation.ProfilerInstrumenter;
import kanela.agent.util.annotation.Experimental;
import kanela.agent.util.conf.KanelaConfiguration.ProfilerConfig;
import kanela.agent.util.log.Logger;
import lombok.SneakyThrows;
import lombok.Value;

@Value
@Experimental
public class KanelaProfiler {

    private ProfilerInstrumenter profilerInstrumenter;
    private ProfilerConfig profilerConfig;

    private KanelaProfiler(Instrumentation instrumentation, ProfilerConfig profilerConfig) {
        this.profilerInstrumenter = ProfilerInstrumenter.of(instrumentation, profilerConfig);
        this.profilerConfig = profilerConfig;
        if (profilerConfig.getEnabled()) {
            Logger.info(() -> "Profiler activated");
        } else {
            Logger.info(() -> "Profiler is not activated");
        }
    }

    public static KanelaProfiler of(Instrumentation instrumentation,
        ProfilerConfig profilerConfig) {
        return new KanelaProfiler(instrumentation, profilerConfig);
    }

    public static void attach() {
        SamplingHandler.setSamplingProvider(SamplingProviderImpl.instance());
    }

    public void activate() {
        ifEnable(this.profilerInstrumenter::activate,
            () -> Logger.debug(() -> "Profiler is disabled"));
    }

    public void deactivate() {
        ifEnable(this.profilerInstrumenter::deactivate,
            () -> Logger.debug(() -> "Profiler is disabled"));
    }

    public void start() {
        ifEnable(() -> {
            this.profilerInstrumenter.activate();
            SamplingHandler.start();
            }, () -> Logger.debug(() -> "Profiler is disabled"));
    }

    public void stop() {
        ifEnable(SamplingHandler::stop,
            () -> Logger.debug(() -> "Profiler is disabled"));
    }

    @SneakyThrows
    public void ifEnable(Runnable runnable, Option<Runnable> fallback) {
        if (profilerConfig.getEnabled()) {
            runnable.run();
        } else {
            fallback.forEach(Runnable::run);
        }
    }

    @SneakyThrows
    public void ifEnable(Runnable runnable) {
        ifEnable(runnable, Option.none());
    }

    @SneakyThrows
    public void ifEnable(Runnable runnable, Runnable fallback) {
        ifEnable(runnable, Option.of(fallback));
    }

    //stop

}
