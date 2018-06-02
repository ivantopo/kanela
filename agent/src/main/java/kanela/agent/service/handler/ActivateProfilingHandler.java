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
public class ActivateProfilingHandler extends RouterNanoHTTPD.DefaultHandler {

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
        return NanoHTTPD.Response.Status.OK;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource,
        Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        return getProfiler(uriResource)
            .map((profiler) -> {
                profiler.activate();
                return newFixedLengthResponse(Status.OK, "application/json",
                    "{\"message\": \"Profiler is activated...\"");
            })
            .getOrElse(() -> newFixedLengthResponse(Status.SERVICE_UNAVAILABLE, "application/json",
                "{\"cause\": \"Profiler was not attached...\""));
    }

    private Option<KanelaProfiler> getProfiler(UriResource uriResource) {
        return Option.of(uriResource.initParameter(0, KanelaProfiler.class));
    }
}
