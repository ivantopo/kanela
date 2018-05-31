package kanela.agent.profiler.instrumentation;

import static java.text.MessageFormat.format;
import static kanela.agent.util.classloader.ClassLoaderNameMatcher.isGroovyClassLoader;
import static kanela.agent.util.classloader.ClassLoaderNameMatcher.isKanelaClassLoader;
import static kanela.agent.util.classloader.ClassLoaderNameMatcher.isReflectionClassLoader;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isExtensionClassLoader;
import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.isNative;
import static net.bytebuddy.matcher.ElementMatchers.isSynthetic;
import static net.bytebuddy.matcher.ElementMatchers.isTypeInitializer;
import static net.bytebuddy.matcher.ElementMatchers.nameMatches;
import static net.bytebuddy.matcher.ElementMatchers.not;

import io.vavr.Function0;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import kanela.agent.api.instrumentation.listener.DebugInstrumentationListener;
import kanela.agent.api.instrumentation.listener.DefaultInstrumentationListener;
import kanela.agent.api.instrumentation.listener.dumper.ClassDumperListener;
import kanela.agent.cache.PoolStrategyCache;
import kanela.agent.profiler.instrumentation.advisor.ProfilerAdvisor;
import kanela.agent.resubmitter.PeriodicResubmitter;
import kanela.agent.util.annotation.Experimental;
import kanela.agent.util.conf.KanelaConfiguration.ProfilerConfig;
import kanela.agent.util.log.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.val;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher.Junction;

@Value
@Experimental
public class ProfilerInstrumenter {

  private static final PoolStrategyCache poolStrategyCache = PoolStrategyCache.instance();

  final static Junction<MethodDescription> methodDescription = nameMatches(".*")
      .and(not(isAbstract()))
      .and(not(isNative()))
      .and(not(isSynthetic()))
      .and(not(isTypeInitializer()));
  final static Junction<TypeDescription> typeDescription = not(isInterface()).and(not(isSynthetic()));

  private Instrumentation instrumentation;
  private ProfilerConfig profilerConfig;

  @NonFinal
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Option<ResettableClassFileTransformer> resettableTransformer = Option.none();

  final Function0<AgentBuilder> agentBuilder;

  private ProfilerInstrumenter(Instrumentation instrumentation, ProfilerConfig profilerConfig) {
    this.instrumentation = instrumentation;
    this.profilerConfig = profilerConfig;
    this.agentBuilder = newAgentBuilder(profilerConfig.getWithinPackage()).memoized();
  }

  public static ProfilerInstrumenter of(Instrumentation instrumentation, ProfilerConfig profilerConfig) {
    return new ProfilerInstrumenter(instrumentation, profilerConfig);
  }

  private Function0<AgentBuilder> newAgentBuilder(String withinPackages) {
    val transformer = new AgentBuilder.Transformer.ForAdvice()
        .advice(methodDescription, ProfilerAdvisor.class.getName());

    val byteBuddy = new ByteBuddy()
        .with(TypeValidation.of(true))
        .with(MethodGraph.Compiler.ForDeclaredMethods.INSTANCE);

    val agentBuilder = new AgentBuilder.Default(byteBuddy)
        .with(new PoolStrategyCache())
        .disableClassFormatChanges() // enable restrictions imposed by most VMs and also HotSpot.
//        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
//        .withResubmission(PeriodicResubmitter.instance())
        .enableBootstrapInjection(instrumentation, getTempDir("kanela-profiler"))
        .ignore(not(nameMatches(withinPackages)))
        .or(any(), isExtensionClassLoader())
        .or(any(), isKanelaClassLoader())
        .or(any(), isGroovyClassLoader())
        .or(any(), isReflectionClassLoader())
        .with(ClassDumperListener.instance())
        .with(DefaultInstrumentationListener.instance())
        .with(new AgentBuilder.Listener.Compound(DebugInstrumentationListener.instance()))
        .type(typeDescription)
        .transform(transformer);

    return () -> agentBuilder;
  }

  private File getTempDir(String tempDirPrefix) {
    return Try
        .of(() -> Files.createTempDirectory(tempDirPrefix).toFile())
        .getOrElseThrow(() -> new RuntimeException(format("Cannot create the temporary directory: {0}", tempDirPrefix)));
  }

  public synchronized void activate() {
    Logger.trace(() -> "Activating profiler instrumentation...");
    if (this.isActivated()) {
      Logger.debug(() -> "Trying to activate the instrumentation of Profiler when it was already activated");
    } else {
      this.resettableTransformer = Option.of(agentBuilder.apply().installOn(this.instrumentation));
      Logger.info(() -> "Instrumentation of Profiler was activated");
    }
  }

  public synchronized void deactivate() {
    resettableTransformer.forEach((transformer) -> transformer.reset(this.instrumentation, RedefinitionStrategy.REDEFINITION));
    resettableTransformer = Option.none();
      Logger.info(() -> "Instrumentation of Profiler was deactivated");
  }

  public synchronized Boolean isActivated() {
    return resettableTransformer.isDefined();
  }

}
