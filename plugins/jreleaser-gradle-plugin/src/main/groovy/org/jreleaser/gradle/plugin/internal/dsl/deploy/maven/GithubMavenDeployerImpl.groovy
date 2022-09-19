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
package org.jreleaser.gradle.plugin.internal.dsl.deploy.maven

import groovy.transform.CompileStatic
import org.gradle.api.internal.provider.Providers
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.jreleaser.gradle.plugin.dsl.deploy.maven.GithubMavenDeployer

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 1.3.0
 */
@CompileStatic
class GithubMavenDeployerImpl extends AbstractMavenDeployer implements GithubMavenDeployer {
    final Property<String> repository

    @Inject
    GithubMavenDeployerImpl(ObjectFactory objects) {
        super(objects)
        repository = objects.property(String).convention(Providers.<String> notDefined())
    }

    @Override
    @Internal
    boolean isSet() {
        super.isSet() ||
            repository.present
    }

    org.jreleaser.model.internal.deploy.maven.GithubMavenDeployer toModel() {
        org.jreleaser.model.internal.deploy.maven.GithubMavenDeployer deployer = new org.jreleaser.model.internal.deploy.maven.GithubMavenDeployer()
        fillProperties(deployer)
        if (repository.present) deployer.repository = repository.get()
        deployer
    }
}
