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
package org.jreleaser.gradle.plugin.dsl.upload

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.jreleaser.gradle.plugin.dsl.common.Activatable

/**
 *
 * @author Andres Almiray
 * @since 0.3.0
 */
@CompileStatic
interface Upload extends Activatable {
    NamedDomainObjectContainer<ArtifactoryUploader> getArtifactory()

    NamedDomainObjectContainer<FtpUploader> getFtp()

    NamedDomainObjectContainer<GitlabUploader> getGitea()

    NamedDomainObjectContainer<GitlabUploader> getGitlab()

    NamedDomainObjectContainer<HttpUploader> getHttp()

    NamedDomainObjectContainer<S3Uploader> getS3()

    NamedDomainObjectContainer<ScpUploader> getScp()

    NamedDomainObjectContainer<SftpUploader> getSftp()

    void artifactory(Action<? super NamedDomainObjectContainer<ArtifactoryUploader>> action)

    void ftp(Action<? super NamedDomainObjectContainer<FtpUploader>> action)

    void gitea(Action<? super NamedDomainObjectContainer<GiteaUploader>> action)

    void gitlab(Action<? super NamedDomainObjectContainer<GitlabUploader>> action)

    void http(Action<? super NamedDomainObjectContainer<HttpUploader>> action)

    void s3(Action<? super NamedDomainObjectContainer<S3Uploader>> action)

    void scp(Action<? super NamedDomainObjectContainer<ScpUploader>> action)

    void sftp(Action<? super NamedDomainObjectContainer<SftpUploader>> action)

    void artifactory(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void ftp(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void gitea(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void gitlab(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void http(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void s3(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void scp(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)

    void sftp(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = NamedDomainObjectContainer) Closure<Void> action)
}