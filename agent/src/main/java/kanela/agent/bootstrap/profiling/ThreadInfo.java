package kanela.agent.bootstrap.profiling;

import lombok.Value;

@Value(staticConstructor = "newOne")
public class ThreadInfo {

    String name;
    long id;
}
