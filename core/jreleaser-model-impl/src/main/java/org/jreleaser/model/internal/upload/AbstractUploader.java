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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jreleaser.model.Active;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.common.AbstractModelObject;
import org.jreleaser.model.internal.common.Artifact;
import org.jreleaser.model.internal.common.ExtraProperties;
import org.jreleaser.model.internal.project.Project;
import org.jreleaser.model.internal.util.Artifacts;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jreleaser.model.Constants.KEY_UPLOADER_NAME;
import static org.jreleaser.util.CollectionUtils.listOf;
import static org.jreleaser.util.StringUtils.capitalize;
import static org.jreleaser.util.StringUtils.getClassNameForLowerCaseHyphenSeparatedName;

/**
 * @author Andres Almiray
 * @since 0.3.0
 */
public abstract class AbstractUploader<A extends org.jreleaser.model.api.upload.Uploader, S extends AbstractUploader<A, S>> extends AbstractModelObject<S> implements Uploader<A>, ExtraProperties {
    @JsonIgnore
    protected final String type;
    protected final Map<String, Object> extraProperties = new LinkedHashMap<>();
    @JsonIgnore
    protected String name;
    @JsonIgnore
    protected boolean enabled;
    protected Active active;
    protected int connectTimeout;
    protected int readTimeout;
    protected Boolean artifacts;
    protected Boolean files;
    protected Boolean signatures;
    protected Boolean checksums;

    protected AbstractUploader(String type) {
        this.type = type;
    }

    @Override
    public void merge(S source) {
        this.active = merge(this.active, source.active);
        this.enabled = merge(this.enabled, source.enabled);
        this.name = merge(this.name, source.name);
        this.connectTimeout = merge(this.connectTimeout, source.connectTimeout);
        this.readTimeout = merge(this.readTimeout, source.readTimeout);
        this.artifacts = merge(this.artifacts, source.artifacts);
        this.files = merge(this.files, source.files);
        this.signatures = merge(this.signatures, source.signatures);
        this.checksums = merge(this.checksums, source.checksums);
        setExtraProperties(merge(this.extraProperties, source.extraProperties));
    }

    @Override
    public String getPrefix() {
        return type;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void disable() {
        active = Active.NEVER;
        enabled = false;
    }

    public boolean resolveEnabled(Project project) {
        if (null == active) {
            active = Active.NEVER;
        }
        enabled = active.check(project);
        if (project.isSnapshot() && !isSnapshotSupported()) {
            enabled = false;
        }
        return enabled;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Active getActive() {
        return active;
    }

    @Override
    public void setActive(Active active) {
        this.active = active;
    }

    @Override
    public void setActive(String str) {
        setActive(Active.of(str));
    }

    @Override
    public boolean isActiveSet() {
        return active != null;
    }

    @Override
    public boolean isSnapshotSupported() {
        return true;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public Integer getReadTimeout() {
        return readTimeout;
    }

    @Override
    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    @Override
    public void setExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties.clear();
        this.extraProperties.putAll(extraProperties);
    }

    @Override
    public void addExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties.putAll(extraProperties);
    }

    @Override
    public boolean isArtifacts() {
        return artifacts == null || artifacts;
    }

    @Override
    public void setArtifacts(Boolean artifacts) {
        this.artifacts = artifacts;
    }

    @Override
    public boolean isArtifactsSet() {
        return artifacts != null;
    }

    @Override
    public boolean isFiles() {
        return files == null || files;
    }

    @Override
    public void setFiles(Boolean files) {
        this.files = files;
    }

    @Override
    public boolean isFilesSet() {
        return files != null;
    }

    @Override
    public boolean isSignatures() {
        return signatures == null || signatures;
    }

    @Override
    public void setSignatures(Boolean signatures) {
        this.signatures = signatures;
    }

    @Override
    public boolean isSignaturesSet() {
        return signatures != null;
    }

    @Override
    public boolean isChecksumsSet() {
        return checksums != null;
    }

    @Override
    public boolean isChecksums() {
        return checksums == null || checksums;
    }

    @Override
    public void setChecksums(Boolean checksums) {
        this.checksums = checksums;
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        if (!full && !isEnabled()) return Collections.emptyMap();

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("enabled", isEnabled());
        props.put("active", active);
        props.put("connectTimeout", connectTimeout);
        props.put("readTimeout", readTimeout);
        props.put("artifacts", isArtifacts());
        props.put("files", isFiles());
        props.put("signatures", isSignatures());
        props.put("checksums", isChecksums());
        asMap(full, props);
        props.put("extraProperties", getResolvedExtraProperties());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(this.getName(), props);
        return map;
    }

    protected abstract void asMap(boolean full, Map<String, Object> props);

    @Override
    public List<String> resolveSkipKeys() {
        String skipUpload = "skipUpload";
        String skipUploadByType = skipUpload + capitalize(type);
        String skipUploadByName = skipUploadByType + getClassNameForLowerCaseHyphenSeparatedName(name);
        return listOf(skipUpload, skipUploadByType, skipUploadByName);
    }

    @Override
    public Map<String, Object> artifactProps(JReleaserContext context, Artifact artifact) {
        return artifactProps(context.fullProps(), artifact);
    }

    @Override
    public Map<String, Object> artifactProps(Map<String, Object> props, Artifact artifact) {
        props.put(KEY_UPLOADER_NAME, getName());
        Artifacts.artifactProps(artifact, props);

        Set<String> keys = new LinkedHashSet<>(props.keySet());
        keys.stream()
            .filter(k -> k.contains("skip") || k.contains("Skip"))
            .forEach(props::remove);

        return props;
    }
}
