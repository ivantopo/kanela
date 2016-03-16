/*
 * =========================================================================================
 * Copyright © 2013-2016 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package kamon.servlet.instrumentation.advisor

import kamon.agent.libs.net.bytebuddy.asm.Advice.{ Argument, OnMethodEnter, This }
import kamon.servlet.ServletExtension
import kamon.servlet.instrumentation.mixin.TraceContextAwareExtension
import kamon.trace.TraceContextAware

/**
 * Advisor for javax.servlet.http.HttpServletResponse::setStatus
 * Advisor for javax.servlet.http.HttpServletResponse::sendError
 */
class ResponseStatusAdvisor
object ResponseStatusAdvisor {
  @OnMethodEnter
  def onEnter(@Argument(0) status: Int, @This response: TraceContextAwareExtension): Unit = {
    response.traceContext().collect { ctx ⇒
      ServletExtension.httpServerMetrics.recordResponse(ctx.name, status.toString)
    }
  }
}