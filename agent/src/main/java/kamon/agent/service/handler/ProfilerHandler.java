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

package kamon.agent.service.handler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

@Value
@EqualsAndHashCode(callSuper = false)
public class ProfilerHandler extends RouterNanoHTTPD.DefaultHandler {

    @Override
    public String getText() { return null; }

    @Override
    public String getMimeType() { return "application/json"; }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() { return NanoHTTPD.Response.Status.OK;}

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        String x = "{\"rootElems\":{\"com.intellij.rt.execution.application.AppMainV2$1.run(AppMainV2.java:64)\":{\"stackTraceElement\":{\"declaringClass\":\"com.intellij.rt.execution.application.AppMainV2$1\",\"methodName\":\"run\",\"fileName\":\"AppMainV2.java\",\"lineNumber\":64},\"sampleCount\":6,\"children\":{\"java.io.BufferedReader.readLine(BufferedReader.java:389)\":{\"stackTraceElement\":{\"declaringClass\":\"java.io.BufferedReader\",\"methodName\":\"readLine\",\"fileName\":\"BufferedReader.java\",\"lineNumber\":389},\"sampleCount\":6,\"children\":{\"java.io.BufferedReader.readLine(BufferedReader.java:324)\":{\"stackTraceElement\":{\"declaringClass\":\"java.io.BufferedReader\",\"methodName\":\"readLine\",\"fileName\":\"BufferedReader.java\",\"lineNumber\":324},\"sampleCount\":6,\"children\":{\"java.io.BufferedReader.fill(BufferedReader.java:161)\":{\"stackTraceElement\":{\"declaringClass\":\"java.io.BufferedReader\",\"methodName\":\"fill\",\"fileName\":\"BufferedReader.java\",\"lineNumber\":161},\"sampleCount\":6,\"children\":{\"java.io.InputStreamReader.read(InputStreamReader.java:184)\":{\"stackTraceElement\":{\"declaringClass\":\"java.io.InputStreamReader\",\"methodName\":\"read\",\"fileName\":\"InputStreamReader.java\",\"lineNumber\":184},\"sampleCount\":6,\"children\":{\"sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)\":{\"stackTraceElement\":{\"declaringClass\":\"sun.nio.cs.StreamDecoder\",\"methodName\":\"read\",\"fileName\":\"StreamDecoder.java\",\"lineNumber\":178},\"sampleCount\":6,\"children\":{\"sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)\":{\"stackTraceElement\":{\"declaringClass\":\"sun.nio.cs.StreamDecoder\",\"methodName\":\"implRead\",\"fileName\":\"StreamDecoder.java\",\"lineNumber\":326},\"sampleCount\":6,\"children\":{\"sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)\":{\"stackTraceElement\":{\"declaringClass\":\"sun.nio.cs.StreamDecoder\",\"methodName\":\"readBytes\",\"fileName\":\"StreamDecoder.java\",\"lineNumber\":284},\"sampleCount\":6,\"children\":{\"java.net.SocketInputStream.read(SocketInputStream.java:141)\":{\"stackTraceElement\":{\"declaringClass\":\"java.net.SocketInputStream\",\"methodName\":\"read\",\"fileName\":\"SocketInputStream.java\",\"lineNumber\":141},\"sampleCount\":6,\"children\":{\"java.net.SocketInputStream.read(SocketInputStream.java:170)\":{\"stackTraceElement\":{\"declaringClass\":\"java.net.SocketInputStream\",\"methodName\":\"read\",\"fileName\":\"SocketInputStream.java\",\"lineNumber\":170},\"sampleCount\":6,\"children\":{\"java.net.SocketInputStream.socketRead(SocketInputStream.java:116)\":{\"stackTraceElement\":{\"declaringClass\":\"java.net.SocketInputStream\",\"methodName\":\"socketRead\",\"fileName\":\"SocketInputStream.java\",\"lineNumber\":116},\"sampleCount\":6,\"children\":{\"java.net.SocketInputStream.socketRead0(Native Method)\":{\"stackTraceElement\":{\"declaringClass\":\"java.net.SocketInputStream\",\"methodName\":\"socketRead0\",\"fileName\":\"SocketInputStream.java\",\"lineNumber\":-2},\"sampleCount\":6,\"children\":{}}}}}}}}}}}}}}}}}}}}}}}},\"java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.ref.Finalizer$FinalizerThread\",\"methodName\":\"run\",\"fileName\":\"Finalizer.java\",\"lineNumber\":209},\"sampleCount\":6,\"children\":{\"java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:164)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.ref.ReferenceQueue\",\"methodName\":\"remove\",\"fileName\":\"ReferenceQueue.java\",\"lineNumber\":164},\"sampleCount\":6,\"children\":{\"java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:143)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.ref.ReferenceQueue\",\"methodName\":\"remove\",\"fileName\":\"ReferenceQueue.java\",\"lineNumber\":143},\"sampleCount\":6,\"children\":{\"java.lang.Object.wait(Native Method)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.Object\",\"methodName\":\"wait\",\"fileName\":\"Object.java\",\"lineNumber\":-2},\"sampleCount\":6,\"children\":{}}}}}}}},\"java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.ref.Reference$ReferenceHandler\",\"methodName\":\"run\",\"fileName\":\"Reference.java\",\"lineNumber\":153},\"sampleCount\":6,\"children\":{\"java.lang.ref.Reference.tryHandlePending(Reference.java:191)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.ref.Reference\",\"methodName\":\"tryHandlePending\",\"fileName\":\"Reference.java\",\"lineNumber\":191},\"sampleCount\":6,\"children\":{\"java.lang.Object.wait(Object.java:502)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.Object\",\"methodName\":\"wait\",\"fileName\":\"Object.java\",\"lineNumber\":502},\"sampleCount\":6,\"children\":{\"java.lang.Object.wait(Native Method)\":{\"stackTraceElement\":{\"declaringClass\":\"java.lang.Object\",\"methodName\":\"wait\",\"fileName\":\"Object.java\",\"lineNumber\":-2},\"sampleCount\":6,\"children\":{}}}}}}}}},\"samplingInProgress\":false}\n";

        return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", x);
    }
}
