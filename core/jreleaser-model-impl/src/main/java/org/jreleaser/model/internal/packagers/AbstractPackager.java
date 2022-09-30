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
package org.jreleaser.model.internal.packagers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jreleaser.model.Active;
import org.jreleaser.model.Stereotype;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.common.AbstractModelObject;
import org.jreleaser.model.internal.common.Artifact;
import org.jreleaser.model.internal.distributions.Distribution;
import org.jreleaser.model.internal.project.Project;
import org.jreleaser.util.FileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class AbstractPackager<A extends org.jreleaser.model.api.packagers.Packager, S extends AbstractPackager<A, S>> extends AbstractModelObject<S> implements Packager<A> {
    @JsonIgnore
    protected final String type;
    protected final Map<String, Object> extraProperties = new LinkedHashMap<>();
    @JsonIgnore
    protected boolean enabled;
    protected Active active;
    protected Boolean continueOnError;
    protected String downloadUrl;
    @JsonIgnore
    protected boolean failed;

    protected AbstractPackager(String type) {
        this.type = type;
    }

    @Override
    public void merge(S source) {
        this.active = merge(this.active, source.active);
        this.enabled = merge(this.enabled, source.enabled);
        this.continueOnError = merge(this.continueOnError, source.continueOnError);
        this.downloadUrl = merge(this.downloadUrl, source.downloadUrl);
        this.failed = source.failed;
        setExtraProperties(merge(this.extraProperties, source.extraProperties));
    }

    @Override
    public Set<Stereotype> getSupportedStereotypes() {
        return EnumSet.allOf(Stereotype.class);
    }

    @Override
    public void fail() {
        this.failed = true;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    public List<Artifact> resolveCandidateArtifacts(JReleaserContext context, Distribution distribution) {
        List<String> fileExtensions = new ArrayList<>(getSupportedFileExtensions(distribution.getType()));
        fileExtensions.sort(naturalOrder());

        return distribution.getArtifacts().stream()
            .filter(Artifact::isActive)
            .filter(artifact -> fileExtensions.stream().anyMatch(ext -> artifact.getResolvedPath(context, distribution).toString().endsWith(ext)))
            .filter(artifact -> supportsPlatform(artifact.getPlatform()))
            .filter(this::isNotSkipped)
            .sorted(Artifact.comparatorByPlatform().thenComparingInt(artifact -> {
                String ext = FileType.getExtension(artifact.getResolvedPath(context, distribution));
                return fileExtensions.indexOf(ext);
            }))
            .collect(toList());
    }

    protected abstract boolean isNotSkipped(Artifact artifact);

    @Override
    public boolean isSnapshotSupported() {
        return false;
    }

    @Override
    public String getPrefix() {
        return getType();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void disable() {
        active = Active.NEVER;
        enabled = false;
    }

    @Override
    public boolean isContinueOnError() {
        return continueOnError != null && continueOnError;
    }

    @Override
    public void setContinueOnError(Boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    @Override
    public boolean isContinueOnErrorSet() {
        return continueOnError != null;
    }

    public boolean resolveEnabled(Project project) {
        if (null == active) {
            active = Active.NEVER;
        }
        enabled = active.check(project);

        return enabled;
    }

    public boolean resolveEnabled(Project project, Distribution distribution) {
        if (null == active) {
            active = Active.NEVER;
        }
        enabled = active.check(project);
        if (!supportsDistribution(distribution.getType())) {
            enabled = false;
        }
        return enabled;
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
    public String getType() {
        return type;
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
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        if (!full && !isEnabled()) return Collections.emptyMap();

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("enabled", isEnabled());
        props.put("active", active);
        props.put("continueOnError", isContinueOnError());
        props.put("downloadUrl", downloadUrl);
        asMap(full, props);
        props.put("extraProperties", getResolvedExtraProperties());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(getType(), props);
        return map;
    }

    protected abstract void asMap(boolean full, Map<String, Object> props);
}
