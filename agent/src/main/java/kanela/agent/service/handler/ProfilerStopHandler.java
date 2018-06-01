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
package kanela.agent.service.handler;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;
import io.vavr.control.Option;
import java.util.Map;
import kanela.agent.profiler.KanelaProfiler;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProfilerStopHandler extends RouterNanoHTTPD.DefaultHandler {

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return Status.OK;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource,
        Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        return getProfiler(uriResource)
            .map((profiler) -> {
                profiler.stop();
                return newFixedLengthResponse(Status.OK, "application/json",
                    "{\"message\": \"Profiler deactivated...\"");
            })
            .getOrElse(() -> newFixedLengthResponse(Status.SERVICE_UNAVAILABLE, "application/json",
                "{\"cause\": \"Profiler wasn't started...\""));
    }

    private Option<KanelaProfiler> getProfiler(UriResource uriResource) {
        return Option.of(uriResource.initParameter(0, KanelaProfiler.class));
    }
}
