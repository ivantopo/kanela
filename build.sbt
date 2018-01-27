/*
 * =========================================================================================
 * Copyright © 2013-2018 the kamon project <http://kamon.io/>
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

import microsites._

name := "kanela-microsite"
scalaVersion := "2.12.3"

// Settings for sbt-microsites https://47deg.github.io/sbt-microsites/
micrositeName             := "Kanela"
micrositeDescription      := "Kanela: The Kamon Instrumentation Agent"
micrositeAuthor           := "kamon-io"
micrositeGithubOwner      := "kamon-io"
micrositeGithubRepo       := "kamon-agent"
micrositeGitterChannel    := true
micrositeGitterChannelUrl := "kamon-io/Kamon"
micrositeBaseUrl          := "/kanela"
micrositeHighlightTheme   := "atom-one-light"

micrositePalette := Map(
  "brand-primary"     -> "#42b263",
  "brand-secondary"   -> "#2ab253",
  "brand-tertiary"    -> "#2ab253",
  "gray-dark"         -> "#453E46",
  "gray"              -> "#837F84",
  "gray-light"        -> "#E3E2E3",
  "gray-lighter"      -> "#F4F3F4",
  "white-color"       -> "#FFFFFF"
)

micrositeConfigYaml := ConfigYml(yamlInline = "code_examples_folder: '_data/examples'")

micrositeExtraMdFiles := Map(
  file("Interceptors.md") -> ExtraMdFileConfig("interceptors.md", "page", Map("title" -> "Interceptors", "section" -> "interceptors", "position" -> "1")),
  file("Mixins.md") -> ExtraMdFileConfig("mixins.md", "page", Map("title" -> "Mixins", "section" -> "mixins", "position" -> "2")),
  file("Bridges.md") -> ExtraMdFileConfig("bridges.md", "page", Map("title" -> "Bridges", "section" -> "bridges", "position" -> "3")),
  file("CHANGELOG.md") -> ExtraMdFileConfig("changelog.md", "page", Map("title" -> "Changelog", "section" -> "changelog", "position" -> "4")),
  file("LICENSE")      -> ExtraMdFileConfig("license.md",   "page", Map("title" -> "License",   "section" -> "license",   "position" -> "5"))
)

enablePlugins(MicrositesPlugin)
