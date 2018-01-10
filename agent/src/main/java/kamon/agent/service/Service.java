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

package kamon.agent.service;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import kamon.agent.service.handler.ProfilerHandler;
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

    public Service() {
        super(9091);
        addMappings();
        setAsyncRunner(new BoundRunner(Executors.newFixedThreadPool(2)));
        System.out.println("\nRunning! Point your browers to http://localhost:" + 9091 + "/ \n");
    }

    @Override
    public void addMappings() {
        super.addMappings();

        addRoute("/profiler/:duration/:frequency/:callback-url", ProfilerHandler.class);
    }


    public static void main(String... args) throws IOException {
        Service service = new Service();
        service.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);



//        ServerRunner.run(Service.class);



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

