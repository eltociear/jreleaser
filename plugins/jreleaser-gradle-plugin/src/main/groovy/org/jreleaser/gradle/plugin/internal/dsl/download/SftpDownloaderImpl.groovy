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
package org.jreleaser.gradle.plugin.internal.dsl.download

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.jreleaser.gradle.plugin.dsl.download.SftpDownloader

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 1.1.0
 */
@CompileStatic
class SftpDownloaderImpl extends AbstractSshDownloader implements SftpDownloader {
    String name

    @Inject
    SftpDownloaderImpl(ObjectFactory objects) {
        super(objects)
    }

    org.jreleaser.model.internal.download.SftpDownloader toModel() {
        org.jreleaser.model.internal.download.SftpDownloader sftp = new org.jreleaser.model.internal.download.SftpDownloader()
        sftp.name = name
        fillProperties(sftp)
        sftp
    }
}
