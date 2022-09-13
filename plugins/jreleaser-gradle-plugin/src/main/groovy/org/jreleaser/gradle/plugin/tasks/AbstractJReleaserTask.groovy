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
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.options.Option
import org.jreleaser.engine.context.ContextCreator
import org.jreleaser.gradle.plugin.JReleaserExtension
import org.jreleaser.logging.JReleaserLogger
import org.jreleaser.model.JReleaserContext
import org.jreleaser.model.JReleaserModel
import org.jreleaser.model.JReleaserVersion
import org.jreleaser.util.PlatformUtils
import org.jreleaser.util.StringUtils

import javax.inject.Inject

import static org.jreleaser.model.JReleaserContext.Configurer
import static org.jreleaser.model.JReleaserContext.Mode

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
abstract class AbstractJReleaserTask extends DefaultTask {
    @Input
    final Property<Boolean> dryrun
    @Input
    final Property<Boolean> gitRootSearch

    @Input
    final DirectoryProperty outputDirectory

    @Internal
    final Property<JReleaserModel> model

    @Internal
    final Property<JReleaserLogger> jlogger

    @Internal
    Mode mode

    @Inject
    AbstractJReleaserTask(ObjectFactory objects) {
        model = objects.property(JReleaserModel)
        jlogger = objects.property(JReleaserLogger)
        mode = Mode.FULL
        dryrun = objects.property(Boolean).convention(false)
        gitRootSearch = objects.property(Boolean).convention(false)
        outputDirectory = objects.directoryProperty()
    }

    @Option(option = 'dry-run', description = 'Skip remote operations (OPTIONAL).')
    void setDryrun(boolean dryrun) {
        this.dryrun.set(dryrun)
    }

    @Option(option = 'git-root-search', description = 'Searches for the Git root (OPTIONAL).')
    void setGitRootSearch(boolean gitRootSearch) {
        this.gitRootSearch.set(gitRootSearch)
    }

    protected JReleaserContext createContext() {
        JReleaserLogger logger = jlogger.get()
        PlatformUtils.resolveCurrentPlatform(logger)

        logger.info('JReleaser {}', JReleaserVersion.getPlainVersion())
        JReleaserVersion.banner(logger.getTracer())
        logger.increaseIndent()
        logger.info('- basedir set to {}', project.projectDir.toPath().toAbsolutePath())
        logger.decreaseIndent()

        return ContextCreator.create(
            logger,
            resolveConfigurer(project.extensions.findByType(JReleaserExtension)),
            mode,
            model.get(),
            project.projectDir.toPath(),
            outputDirectory.get().asFile.toPath(),
            dryrun.get(),
            gitRootSearch.get(),
            collectSelectedPlatforms())
    }

    protected List<String> collectSelectedPlatforms() {
        []
    }

    protected Configurer resolveConfigurer(JReleaserExtension extension) {
        if (!extension.configFile.present) return Configurer.GRADLE

        File configFile = extension.configFile.get().asFile
        switch (StringUtils.getFilenameExtension(configFile.name)) {
            case 'yml':
            case 'yaml':
                return Configurer.CLI_YAML
            case 'toml':
                return Configurer.CLI_TOML
            case 'json':
                return Configurer.CLI_JSON
        }
        // should not happen!
        throw new IllegalArgumentException('Invalid configuration format: ' + configFile.name)
    }
}
