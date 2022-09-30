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
package org.jreleaser.model.internal.assemble;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jreleaser.model.Active;
import org.jreleaser.model.Stereotype;
import org.jreleaser.model.internal.common.AbstractModelObject;
import org.jreleaser.model.internal.common.Artifact;
import org.jreleaser.model.internal.common.FileSet;
import org.jreleaser.model.internal.platform.Platform;
import org.jreleaser.model.internal.project.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jreleaser.model.Constants.KEY_DISTRIBUTION_NAME;
import static org.jreleaser.model.Constants.KEY_DISTRIBUTION_STEREOTYPE;
import static org.jreleaser.mustache.MustacheUtils.applyTemplates;

/**
 * @author Andres Almiray
 * @since 0.2.0
 */
public abstract class AbstractAssembler<S extends AbstractAssembler<S, A>, A extends org.jreleaser.model.api.assemble.Assembler> extends AbstractModelObject<S> implements Assembler<A> {
    @JsonIgnore
    protected final Set<Artifact> outputs = new LinkedHashSet<>();
    protected final Map<String, Object> extraProperties = new LinkedHashMap<>();
    protected final List<FileSet> fileSets = new ArrayList<>();
    protected final Platform platform = new Platform();
    @JsonIgnore
    protected final String type;
    @JsonIgnore
    protected String name;
    @JsonIgnore
    protected boolean enabled;
    protected Active active;
    protected Boolean exported;
    private Stereotype stereotype;

    protected AbstractAssembler(String type) {
        this.type = type;
    }

    @Override
    public void merge(S source) {
        this.active = merge(this.active, source.active);
        this.enabled = merge(this.enabled, source.enabled);
        this.exported = merge(this.exported, source.exported);
        this.name = merge(this.name, source.name);
        this.platform.merge(source.platform);
        this.stereotype = merge(this.stereotype, source.getStereotype());
        setOutputs(merge(this.outputs, source.outputs));
        setFileSets(merge(this.fileSets, source.fileSets));
        setExtraProperties(merge(this.extraProperties, source.extraProperties));
    }

    public Map<String, Object> props() {
        Map<String, Object> props = new LinkedHashMap<>();
        applyTemplates(props, getResolvedExtraProperties());
        props.put(KEY_DISTRIBUTION_NAME, name);
        props.put(KEY_DISTRIBUTION_STEREOTYPE, getStereotype());
        return props;
    }

    @Override
    public Stereotype getStereotype() {
        return stereotype;
    }

    @Override
    public void setStereotype(Stereotype stereotype) {
        this.stereotype = stereotype;
    }

    @Override
    public void setStereotype(String str) {
        setStereotype(Stereotype.of(str));
    }

    @Override
    public String getType() {
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
        return enabled;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void setPlatform(Platform platform) {
        this.platform.merge(platform);
    }

    @Override
    public boolean isExported() {
        return exported == null || exported;
    }

    @Override
    public void setExported(Boolean exported) {
        this.exported = exported;
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
    public Set<Artifact> getOutputs() {
        return Artifact.sortArtifacts(outputs);
    }

    @Override
    public void setOutputs(Set<Artifact> output) {
        this.outputs.clear();
        this.outputs.addAll(output);
    }

    @Override
    public void addOutput(Artifact artifact) {
        if (null != artifact) {
            this.outputs.add(artifact);
        }
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
    public String getPrefix() {
        return getType();
    }

    @Override
    public List<FileSet> getFileSets() {
        return fileSets;
    }

    @Override
    public void setFileSets(List<FileSet> fileSets) {
        this.fileSets.clear();
        this.fileSets.addAll(fileSets);
    }

    @Override
    public void addFileSets(List<FileSet> files) {
        this.fileSets.addAll(files);
    }

    @Override
    public void addFileSet(FileSet file) {
        if (null != file) {
            this.fileSets.add(file);
        }
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        if (!full && !isEnabled()) return Collections.emptyMap();

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("enabled", isEnabled());
        props.put("exported", isExported());
        props.put("active", active);
        props.put("stereotype", stereotype);
        if (full || platform.isSet()) props.put("platform", platform.asMap(full));
        asMap(full, props);
        Map<String, Map<String, Object>> mappedFileSets = new LinkedHashMap<>();
        for (int i = 0; i < fileSets.size(); i++) {
            mappedFileSets.put("fileSet " + i, fileSets.get(i).asMap(full));
        }
        props.put("fileSets", mappedFileSets);
        props.put("extraProperties", getResolvedExtraProperties());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(getName(), props);
        return map;
    }

    protected abstract void asMap(boolean full, Map<String, Object> props);
}
