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
import org.gradle.api.internal.provider.Providers
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.jreleaser.gradle.plugin.dsl.packagers.Packager
import org.jreleaser.model.Active

import javax.inject.Inject

import static org.jreleaser.util.StringUtils.isNotBlank

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
abstract class AbstractPackager implements Packager {
    final Property<Active> active
    final Property<Boolean> continueOnError
    final Property<String> downloadUrl
    final MapProperty<String, Object> extraProperties

    @Inject
    AbstractPackager(ObjectFactory objects) {
        active = objects.property(Active).convention(Providers.<Active> notDefined())
        continueOnError = objects.property(Boolean).convention(Providers.<Boolean> notDefined())
        downloadUrl = objects.property(String).convention(Providers.<String> notDefined())
        extraProperties = objects.mapProperty(String, Object).convention(Providers.notDefined())
    }

    @Internal
    boolean isSet() {
        active.present ||
            continueOnError.present ||
            downloadUrl.present ||
            extraProperties.present
    }

    @Override
    void setActive(String str) {
        if (isNotBlank(str)) {
            active.set(Active.of(str.trim()))
        }
    }

    protected <T extends org.jreleaser.model.internal.packagers.Packager> void fillPackagerProperties(T packager) {
        if (active.present) packager.active = active.get()
        if (continueOnError.present) packager.continueOnError = continueOnError.get()
        if (downloadUrl.present) packager.downloadUrl = downloadUrl.get()
        if (extraProperties.present) packager.extraProperties.putAll(extraProperties.get())
    }
}
