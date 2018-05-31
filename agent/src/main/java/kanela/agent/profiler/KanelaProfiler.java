package kanela.agent.profiler;

import java.lang.instrument.Instrumentation;
import kanela.agent.profiler.instrumentation.ProfilerInstrumenter;
import kanela.agent.util.annotation.Experimental;
import kanela.agent.util.conf.KanelaConfiguration.ProfilerConfig;
import kanela.agent.util.log.Logger;
import lombok.Value;
import lombok.val;

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

  public static KanelaProfiler of(Instrumentation instrumentation, ProfilerConfig profilerConfig) {
      return new KanelaProfiler(instrumentation, profilerConfig);
  }

  public void start() {
      // FIXME improve!
      if (profilerConfig.getEnabled())
        this.profilerInstrumenter.activate();
      else {
        Logger.debug(() -> "Profiler is disabled");
      }
  }


  //stop

}
