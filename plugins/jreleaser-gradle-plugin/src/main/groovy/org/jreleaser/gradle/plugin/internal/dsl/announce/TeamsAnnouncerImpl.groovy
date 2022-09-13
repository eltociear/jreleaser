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
package org.jreleaser.gradle.plugin.internal.dsl.announce

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.provider.Providers
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.jreleaser.gradle.plugin.dsl.announce.TeamsAnnouncer

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.2.0
 */
@CompileStatic
class TeamsAnnouncerImpl extends AbstractAnnouncer implements TeamsAnnouncer {
    final Property<String> webhook
    final RegularFileProperty messageTemplate

    @Inject
    TeamsAnnouncerImpl(ObjectFactory objects) {
        super(objects)
        webhook = objects.property(String).convention(Providers.<String> notDefined())
        messageTemplate = objects.fileProperty().convention(Providers.notDefined())
    }

    @Override
    void setMessageTemplate(String messageTemplate) {
        this.messageTemplate.set(new File(messageTemplate))
    }

    @Override
    @Internal
    boolean isSet() {
        super.isSet() ||
            webhook.present ||
            messageTemplate.present
    }

    org.jreleaser.model.internal.announce.TeamsAnnouncer toModel() {
        org.jreleaser.model.internal.announce.TeamsAnnouncer teams = new org.jreleaser.model.internal.announce.TeamsAnnouncer()
        fillProperties(teams)
        if (webhook.present) teams.webhook = webhook.get()
        if (messageTemplate.present) {
            teams.messageTemplate = messageTemplate.asFile.get().absolutePath
        }
        teams
    }
}
