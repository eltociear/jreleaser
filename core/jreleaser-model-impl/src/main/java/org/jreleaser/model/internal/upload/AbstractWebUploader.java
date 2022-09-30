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
package org.jreleaser.model.internal.upload;

import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.common.Artifact;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.jreleaser.mustache.Templates.resolveTemplate;

/**
 * @author Andres Almiray
 * @since 0.8.0
 */
public abstract class AbstractWebUploader<A extends org.jreleaser.model.api.upload.WebUploader, S extends AbstractWebUploader<A, S>> extends AbstractUploader<A, S> implements WebUploader<A> {
    protected String uploadUrl;
    protected String downloadUrl;

    protected AbstractWebUploader(String type) {
        super(type);
    }

    @Override
    public void merge(S source) {
        super.merge(source);
        this.uploadUrl = merge(this.uploadUrl, source.uploadUrl);
        this.downloadUrl = merge(this.downloadUrl, source.downloadUrl);
    }

    @Override
    public String getResolvedDownloadUrl(JReleaserContext context, Artifact artifact) {
        return getResolvedDownloadUrl(context.fullProps(), artifact);
    }

    @Override
    public String getResolvedDownloadUrl(Map<String, Object> props, Artifact artifact) {
        Map<String, Object> p = new LinkedHashMap<>(artifactProps(props, artifact));
        p.putAll(getResolvedExtraProperties());
        return resolveTemplate(downloadUrl, p);
    }

    @Override
    public String getResolvedUploadUrl(JReleaserContext context, Artifact artifact) {
        Map<String, Object> p = new LinkedHashMap<>(artifactProps(context, artifact));
        p.putAll(getResolvedExtraProperties());
        return resolveTemplate(uploadUrl, p);
    }

    @Override
    public String getUploadUrl() {
        return uploadUrl;
    }

    @Override
    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    protected void asMap(boolean full, Map<String, Object> props) {
        props.put("uploadUrl", uploadUrl);
        props.put("downloadUrl", downloadUrl);
    }
}
