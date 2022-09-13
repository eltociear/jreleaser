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
package org.jreleaser.gradle.plugin.internal.dsl.packagers

import groovy.transform.CompileStatic
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.internal.provider.Providers
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Internal
import org.jreleaser.gradle.plugin.dsl.packagers.TemplatePackager

import javax.inject.Inject

import static org.jreleaser.util.StringUtils.isNotBlank

/**
 *
 * @author Andres Almiray
 * @since 0.6.0
 */
@CompileStatic
abstract class AbstractTemplatePackager extends AbstractPackager implements TemplatePackager {
    final DirectoryProperty templateDirectory
    final ListProperty<String> skipTemplates

    @Inject
    AbstractTemplatePackager(ObjectFactory objects) {
        super(objects)
        templateDirectory = objects.directoryProperty().convention(Providers.notDefined())
        skipTemplates = objects.listProperty(String).convention(Providers.<List<String>> notDefined())
    }

    @Internal
    boolean isSet() {
        super.isSet() ||
            templateDirectory.present ||
            skipTemplates.present
    }

    @Override
    void skipTemplate(String template) {
        if (isNotBlank(template)) {
            skipTemplates.add(template.trim())
        }
    }

    protected <T extends org.jreleaser.model.internal.packagers.TemplatePackager> void fillTemplatePackagerProperties(T packager) {
        if (templateDirectory.present) {
            packager.templateDirectory = templateDirectory.get().asFile.toPath().toAbsolutePath().toString()
        }
        packager.skipTemplates = (List<String>) skipTemplates.getOrElse([])
    }
}
