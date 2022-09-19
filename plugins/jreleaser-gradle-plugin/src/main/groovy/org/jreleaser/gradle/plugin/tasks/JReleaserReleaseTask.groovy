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
package org.jreleaser.gradle.plugin.tasks

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.jreleaser.model.internal.JReleaserContext
import org.jreleaser.workflow.Workflows

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
abstract class JReleaserReleaseTask extends AbstractJReleaserDistributionTask {
    @Input
    @Optional
    final ListProperty<String> deployerTypes

    @Input
    @Optional
    final ListProperty<String> excludedDeployerTypes

    @Input
    @Optional
    final ListProperty<String> deployerNames

    @Input
    @Optional
    final ListProperty<String> excludedDeployerNames
    
    @Input
    @Optional
    final ListProperty<String> uploaderTypes

    @Input
    @Optional
    final ListProperty<String> excludedUploaderTypes

    @Input
    @Optional
    final ListProperty<String> uploaderNames

    @Input
    @Optional
    final ListProperty<String> excludedUploaderNames

    @Inject
    JReleaserReleaseTask(ObjectFactory objects) {
        super(objects)
        deployerTypes = objects.listProperty(String).convention([])
        excludedDeployerTypes = objects.listProperty(String).convention([])
        deployerNames = objects.listProperty(String).convention([])
        excludedDeployerNames = objects.listProperty(String).convention([])
        uploaderTypes = objects.listProperty(String).convention([])
        excludedUploaderTypes = objects.listProperty(String).convention([])
        uploaderNames = objects.listProperty(String).convention([])
        excludedUploaderNames = objects.listProperty(String).convention([])
    }

    @Option(option = 'deployer', description = 'Include a deployer by type (OPTIONAL).')
    void setDeployerType(List<String> deployerTypes) {
        this.deployerTypes.set(deployerTypes)
    }

    @Option(option = 'exclude-deployer', description = 'Exclude a deployer by type (OPTIONAL).')
    void setExcludeDeployerType(List<String> excludedDeployerTypes) {
        this.excludedDeployerTypes.set(excludedDeployerTypes)
    }

    @Option(option = 'deployer-name', description = 'Include a deployer by name (OPTIONAL).')
    void setDeployerName(List<String> deployerNames) {
        this.deployerNames.set(deployerNames)
    }

    @Option(option = 'exclude-deployer-name', description = 'Exclude a deployer by name (OPTIONAL).')
    void setExcludeDeployerName(List<String> excludedDeployerNames) {
        this.excludedDeployerNames.set(excludedDeployerNames)
    }
    
    @Option(option = 'uploader', description = 'Include an uploader by type (OPTIONAL).')
    void setUploaderType(List<String> uploaderTypes) {
        this.uploaderTypes.set(uploaderTypes)
    }

    @Option(option = 'exclude-uploader', description = 'Exclude an uploader by type (OPTIONAL).')
    void setExcludeUploaderType(List<String> excludedUploaderTypes) {
        this.excludedUploaderTypes.set(excludedUploaderTypes)
    }

    @Option(option = 'uploader-name', description = 'Include an uploader by name (OPTIONAL).')
    void setUploaderName(List<String> uploaderNames) {
        this.uploaderNames.set(uploaderNames)
    }

    @Option(option = 'exclude-uploader-name', description = 'Exclude an uploader by name (OPTIONAL).')
    void setExcludeUploaderName(List<String> excludedUploaderNames) {
        this.excludedUploaderNames.set(excludedUploaderNames)
    }

    @TaskAction
    void performAction() {
        Workflows.release(setupContext()).execute()
    }

    protected JReleaserContext setupContext() {
        JReleaserContext ctx = super.setupContext()
        ctx.includedDeployerTypes = deployerTypes.orNull
        ctx.excludedDeployerTypes = excludedDeployerTypes.orNull
        ctx.includedDeployerNames = deployerNames.orNull
        ctx.excludedDeployerNames = excludedDeployerNames.orNull
        ctx.includedUploaderTypes = uploaderTypes.orNull
        ctx.excludedUploaderTypes = excludedUploaderTypes.orNull
        ctx.includedUploaderNames = uploaderNames.orNull
        ctx.excludedUploaderNames = excludedUploaderNames.orNull
        ctx
    }
}
