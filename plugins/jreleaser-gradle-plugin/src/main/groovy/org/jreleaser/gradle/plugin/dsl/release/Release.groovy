/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2022 The JReleaser authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jreleaser.gradle.plugin.dsl.release

import groovy.transform.CompileStatic
import org.gradle.api.Action

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
interface Release {
    GithubReleaser getGithub()

    GitlabReleaser getGitlab()

    GiteaReleaser getGitea()

    CodebergReleaser getCodeberg()

    GenericGitReleaser getGeneric()

    void github(Action<? super GithubReleaser> action)

    void gitlab(Action<? super GitlabReleaser> action)

    void gitea(Action<? super GiteaReleaser> action)

    void codeberg(Action<? super CodebergReleaser> action)

    void generic(Action<? super GenericGitReleaser> action)

    void github(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = GithubReleaser) Closure<Void> action)

    void gitlab(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = GitlabReleaser) Closure<Void> action)

    void gitea(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = GiteaReleaser) Closure<Void> action)

    void codeberg(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = CodebergReleaser) Closure<Void> action)

    void generic(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = GenericGitReleaser) Closure<Void> action)
}