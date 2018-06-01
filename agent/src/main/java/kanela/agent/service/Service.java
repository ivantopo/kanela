/*
 * =========================================================================================
 * Copyright © 2013-2018 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */
package kanela.agent.service;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import kanela.agent.profiler.KanelaProfiler;
import kanela.agent.service.handler.ProfilerStartHandler;
import kanela.agent.service.handler.ProfilerStopHandler;
import kanela.agent.util.log.Logger;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Value
@EqualsAndHashCode(callSuper = false)
public class Service extends RouterNanoHTTPD {

  private KanelaProfiler kanelaProfiler;

  private Service(KanelaProfiler kanelaProfiler) {
    super(9091);
    this.kanelaProfiler = kanelaProfiler;
    addMappings();
    setAsyncRunner(new BoundRunner(Executors.newFixedThreadPool(2)));
  }

  public static Service of(KanelaProfiler kanelaProfiler) {
    return new Service(kanelaProfiler);
  }

  @Override
  public void addMappings() {
    super.addMappings();

    addRoute("/profiler/start", ProfilerStartHandler.class, kanelaProfiler);
    addRoute("/profiler/stop", ProfilerStopHandler.class, kanelaProfiler);
  }


  public void run() {
    try {
      this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
      Logger.info(() -> "\nHttp server running! Point your browers to http://localhost:" + 9091 + "/ \n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private class BoundRunner implements AsyncRunner {
    private ExecutorService executorService;
    private final List<NanoHTTPD.ClientHandler> running = Collections.synchronizedList(new ArrayList<NanoHTTPD.ClientHandler>());

    public BoundRunner(ExecutorService executorService) {
      this.executorService = executorService;
    }

    @Override
    public void closeAll() {
      // copy of the list for concurrency
      for (NanoHTTPD.ClientHandler clientHandler : new ArrayList<>(this.running)) {
        clientHandler.close();
      }
    }

    @Override
    public void closed(NanoHTTPD.ClientHandler clientHandler) {
      this.running.remove(clientHandler);
    }

    @Override
    public void exec(NanoHTTPD.ClientHandler clientHandler) {
      executorService.submit(clientHandler);
      this.running.add(clientHandler);
    }
  }
}

